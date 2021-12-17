user_id = 'jdposada'
nero_gcp_project = 'som-nero-nigam-starr'
cdm_project_id = 'som-rit-phi-starr-prod'
cdm_dataset_id = 'starr_omop_cdm5_deid_latest'
work_project_id = nero_gcp_project
work_dataset_id = f'prone_nlp'
cohort_table_id = 'prone_cohort'
cohort_id = 1


# In[ ]:


##Setting up Google SDK environment
import os
# Use this path if you are in your high risk PHI approved MAC personal laptop
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = f'/Users/{user_id}/.config/gcloud/application_default_credentials.json'

##set correct Nero project 
os.environ['GCLOUD_PROJECT'] = nero_gcp_project


# ## Import Packages and BQ Client

# In[ ]:


##Setting up the python environment
import pandas as pd
import numpy as np
import textwrap, re, os, shutil
from tqdm import tqdm
from google.cloud import bigquery
client = bigquery.Client(project=work_project_id);


# Create a working dataset within the user's NERO project

# In[ ]:


client.create_dataset(f"{work_project_id}.{work_dataset_id}", exists_ok=True)


# Helper function to visualize un-formatted notes in a sligthly better way

# In[ ]:


def print_note(note_text):
    my_wrap = textwrap.TextWrapper(width = 80)
    [print(line) for line in my_wrap.wrap(text=note_text)]


# ## Read file with SQL code

# In[ ]:


parametrized_sql = open('sql/1546.sql','r').read()


# In[ ]:


print(parametrized_sql)


# ### Substitute each one of the parameters

# In[ ]:


sql = re.sub(pattern=r'@temp_database_schema', 
             repl=f"`{work_project_id}.{work_dataset_id}`",
             string=parametrized_sql)

sql = re.sub(pattern=r'@target_database_schema', 
             repl=f"`{work_project_id}.{work_dataset_id}`",
             string=sql)

sql = re.sub(pattern=r'@target_cohort_table', 
             repl=cohort_table_id,
             string=sql)

sql = re.sub(pattern=r'@target_cohort_id', 
             repl=f"{cohort_id}",
             string=sql)

sql = re.sub(pattern=r'@vocabulary_database_schema', 
             repl=f"`{cdm_project_id}.{cdm_dataset_id}`",
             string=sql)

sql = re.sub(pattern=r'@cdm_database_schema', 
             repl=f"`{cdm_project_id}.{cdm_dataset_id}`",
             string=sql)


# **Useful Trick**
# Substitute CREATE TABLE statements with CREATE OR REPLACE

# In[ ]:


sql = sql.lower()
sql = re.sub(pattern=r'create table', 
             repl="create or replace table",
             string=sql)


# **Caveat**
# - Cohort table MUST exist before executing this for the first time

# In[ ]:


sql2 = """
CREATE OR REPLACE TABLE
`{work_project_id}.{work_dataset_id}.{cohort_table_id}`
(
cohort_definition_id INT64,
subject_id INT64,
cohort_start_date DATE,
cohort_end_date DATE
)

""".format_map({'work_project_id': work_project_id,
                'work_dataset_id': work_dataset_id,
                'cohort_table_id': cohort_table_id})

client.query(sql2).result();


# ### Execute SQL

# In[ ]:


client.query(sql).result()


# #### Subset tables WITHOUT end date

# In[ ]:


tables_without_end_date = [
                        #    "measurement",
                           "note", 
                        #    "observation",
                        #    "procedure_occurrence"
                           ]

date_column_prefix = [
                    #   "measurement",
                      "note",
                    #   "observation",
                    #   "procedure"
                      ]

concept_id_prefix = [
                    #  "measurement",
                     "note_class",
                    #  "observation",
                    #  "procedure"
                     ]


# In[ ]:


for i in range(len(tables_without_end_date)):
    
    table_id = tables_without_end_date[i]
    prefix_date = date_column_prefix[i]
    concept_id_prefix_table = concept_id_prefix[i]
    
    print(f'Subsetting {table_id}')
    
    sql = """
    CREATE OR REPLACE TABLE
     `{work_project_id}.{work_dataset_id}.{table_id}` AS
    SELECT
    c.concept_name as _{concept_id_prefix}_name,
    table_.*
    FROM
     `{cdm_project_id}.{cdm_dataset_id}.{table_id}` table_
    JOIN
     `{cdm_project_id}.{cdm_dataset_id}.concept` c
    ON
     c.concept_id = table_.{concept_id_prefix}_concept_id
    JOIN
     `{work_project_id}.{work_dataset_id}.{cohort_table_id}` cohort
    ON
     table_.person_id = cohort.subject_id
    WHERE
     cohort_definition_id = {cohort_id}
     AND table_.{prefix_date}_date 
     BETWEEN cohort.cohort_start_date AND cohort.cohort_end_date
    """.format_map({'work_project_id': work_project_id,
                    'work_dataset_id': work_dataset_id,
                    'cdm_project_id': cdm_project_id,
                    'cdm_dataset_id': cdm_dataset_id,
                    'cohort_table_id': cohort_table_id,
                    'cohort_id': cohort_id,
                    'table_id': table_id,
                    'prefix_date': prefix_date,
                    'concept_id_prefix': concept_id_prefix_table})
    client.query(sql).result();


# In[ ]:


sql = f"""
CREATE OR REPLACE TABLE
  `som-nero-nigam-starr.prone_nlp.note_prone_inpatient` AS
SELECT
  note.*,
  DATE_DIFF(note.note_DATE, cohort.cohort_start_date, DAY) AS dates_after_index
FROM
  `som-nero-nigam-starr.prone_nlp.note` note
JOIN
  `som-nero-nigam-starr.prone_nlp.prone_cohort` cohort
ON
  cohort.subject_id = note.person_id
WHERE
  regexp_contains(lower(note_text), r"(prone|proned|deprone|deproned|unproned|unprone|proning|proneing|rotaprone|rotoprone|rotabed|rotobed)")
  AND note_date BETWEEN cohort.cohort_start_date
  AND DATE_ADD(cohort.cohort_start_date, INTERVAL 90 DAY)
"""

print(sql)
# client.query(sql).result();


# In[ ]:


sql = """
SELECT
 person_id,
 CONCAT(EXTRACT(YEAR from note_date), '_', EXTRACT(MONTH from note_date), '_', EXTRACT(DAY from note_date)) as note_date,
 note_id,
 note_text
FROM
 `som-nero-nigam-starr.prone_nlp.note_prone_inpatient`
"""

df_notes = client.query(sql).to_dataframe()


# In[ ]:


df_notes.shape


# In[ ]:


size = 50
person_id_array = df_notes['person_id'].values
selected_person_id = np.random.choice(person_id_array, size=size, replace=False, p=None)
cond = (df_notes['person_id'].isin(selected_person_id.tolist()))
subset_notes_df = df_notes.loc[cond, :]
subset_notes_df.shape


# In[ ]:


notes_folder = "/Users/jdposada/Documents/prone_dist_20210603/stanford_notes/20210714_notes_folder_sample"
if not os.path.exists(notes_folder):
    os.makedirs(notes_folder)
else:
    shutil.rmtree(notes_folder)
    os.makedirs(notes_folder)

output_folder = "/Users/jdposada/Documents/prone_dist_20210603/stanford_notes/20210714_notes_folder_sample_output"
if not os.path.exists(output_folder):
    os.makedirs(output_folder)
else:
    shutil.rmtree(output_folder)
    os.makedirs(output_folder)

for i, row in subset_notes_df.iterrows():
    note_text = row['note_text']
    note_id = row['note_id']
    person_id = row['person_id']
    date_string = row['note_date']
    open(f"{notes_folder}/{person_id}_{date_string}_{note_id}.txt", 'w').write(note_text)


# # Process with VA annotator

# In[ ]:


# Run broker UIMA
"/Users/jdposada/Documents/prone_dist_20210603/apache-uima-as-2.9.0/bin/startBroker.sh"


# In[ ]:


# Run Service LEO
# get out of the VPN
"cd Users/jdposada/Documents/prone_dist_20210603/"
"./runService.sh"


# In[ ]:


# Run Client
"cd /Users/jdposada/Documents/prone_dist_20210603/"
"./runClient.sh"


# In[ ]:


file_path = f"{output_folder}/uima_output.csv"

df = pd.read_csv(file_path)

client.load_table_from_dataframe(df, destination=f"{work_project_id}.{work_dataset_id}.20210714_note_prone_inpatient_processed_sample")


# In[ ]:


# SQL to parse notes dates , note_id and person_id
sql = """
CREATE OR REPLACE TABLE
  `som-nero-nigam-starr.prone_nlp.20210714_note_prone_inpatient_processed_sample` AS
WITH
  t1 AS (
  SELECT
    SPLIT(DocID, "_")[
  OFFSET
    (0)] AS person_id,
    SPLIT(DocID, "_")[
  OFFSET
    (1)] AS year,
    SPLIT(DocID, "_")[
  OFFSET
    (2)] AS month,
    SPLIT(DocID, "_")[
  OFFSET
    (3)] AS day,
    SPLIT(SPLIT(DocID, "_")[
    OFFSET
      (4)], ".")[
  OFFSET
    (0)] AS note_id,
    * EXCEPT(DocID)
  FROM
    `som-nero-nigam-starr.prone_nlp.20210714_note_prone_inpatient_processed_sample` ),
  t2 AS (
  SELECT
    * EXCEPT(month),
  IF
    (LENGTH(month) > 1,
      month,
      CONCAT("0", month)) AS month
  FROM
    t1 )
SELECT
  * EXCEPT(year,
    month,
    day),
  parse_DATE("%Y%m%e",
    CONCAT(year, month, day)) AS note_date
FROM
  t2
"""

sql = """
CREATE OR REPLACE TABLE
  `som-nero-nigam-starr.prone_nlp.20210714_note_prone_inpatient_processed_sample` AS
SELECT
  *,
IF
  (REGEXP_CONTAINS(Phrase_Modifier,r"Treated.*"),
    1,
    0) AS NLP_Treated,
IF
  (REGEXP_CONTAINS(Phrase_Modifier,r"Intent.*"),
    1,
    0) AS NLP_Intent,
IF
  (REGEXP_CONTAINS(Phrase_Modifier,r"Not.*"),
    1,
    0) AS NLP_Exclude,
IF
  (REGEXP_CONTAINS(Phrase_Modifier,r"Exclude.*"),
    1,
    0) AS NLP_Not_treated,
IF
  (REGEXP_CONTAINS(Phrase_Modifier,r"Surgery.*"),
    1,
    0) AS NLP_surgery
FROM
  `som-nero-nigam-starr.prone_nlp.20210714_note_prone_inpatient_processed_sample`
"""

sql = """
CREATE OR REPLACE TABLE
  `som-nero-nigam-starr.prone_nlp.20210714_note_prone_inpatient_processed_sample_patient_level` AS
SELECT
  person_id,
IF
  (SUM(NLP_Treated)> 0,
    1,
    0) AS NLP_Treated,
IF
  (SUM(NLP_Intent)> 0,
    1,
    0) AS NLP_Intent,
IF
  (SUM(NLP_Exclude)> 0,
    1,
    0) AS NLP_Exclude,
IF
  (SUM(NLP_Not_treated)> 0,
    1,
    0) AS NLP_Not_treated,
IF
  (SUM(NLP_surgery)> 0,
    1,
    0) AS NLP_surgery
FROM
  `som-nero-nigam-starr.prone_nlp.20210714_note_prone_inpatient_processed_sample`
GROUP BY
  person_id
"""

