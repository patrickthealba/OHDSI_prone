

/*
OHDSI COVID Prone Implimentation
Author: Patrick Alba
Date: 2021-08-26
Modifications:	

Requires: 
	LEO Prone NLP Pipeline

Steps:

1. Populate Atlas Cohort Creating Initial target cohort

2. Process notes and create the nlp_raw_output table
	Depending on the hospital/EHR size, prefiltering may be required.
3. Create dataframe of rolledup/aggregate values COVID139_admission_summary
	TODO:Create  instead of target



*/




----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Populate Cohort using Atlas definition COVID_ID 139
----------------------------------------------------------------------------------------------------------------------------------------------------------



	select 
		person_id
		,start_date
		,end_date   
		from 
		 `som-nero-nigam-starr`.




----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Process Notes
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Input query for database reader: 
	--If cohort is small enough all notes with key terms can be processed, otherwise this query is used to select 
	--All notes found 0-30 days from the start_date

	select 
		note_id
		,note_text
		,note_date
		,note_title 
	from 
		`som-rit-phi-starr-prod.starr_omop_cdm5_deid_latest`.note as note
		join  `som-nero-nigam-starr`. as coh
		on note.person_id=coh.person_id
	where 
		DATE_DIFF(IF(SAFE_CAST(note.note_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(note.note_date  AS STRING)),SAFE_CAST(note.note_date  AS DATE)), IF(SAFE_CAST(coh.start_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(coh.start_date  AS STRING)),SAFE_CAST(coh.start_date  AS DATE)), DAY) <=30 
		and DATE_DIFF(IF(SAFE_CAST(note.note_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(note.note_date  AS STRING)),SAFE_CAST(note.note_date  AS DATE)), IF(SAFE_CAST(coh.start_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(coh.start_date  AS STRING)),SAFE_CAST(coh.start_date  AS DATE)), DAY) >=0 


-- Optional:
-- If there are more than several million notes to process within 30 days of the start_date
	-- a separate table with distinct documentids and a rowno can be created to batch the notes using the LEO batcheddatabase reader
	-- If query size is still a limitiation, notes can be prefiltered using only documents that contain key prone terms
	-- If note titles are mapped to HL7/Loin Clinical Document Ontology,  procedure/surgery notes can be more easily removed


----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Output
----------------------------------------------------------------------------------------------------------------------------------------------------------



		select 
				note_id
			  ,term
			  ,term_type
			  ,prone_status
			  ,term_modifier
			  ,phrase_modifier
			  ,modifier_term
			  ,temporality_modifier
			  ,date_1
			  ,date_2
			  ,anchored_sentence
			  ,spanstart
			  ,spanend
			  ,snippets
		  from `som-nero-nigam-starr`.covid139_nlp_output



----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Restrict output to relevant patients and dates, normalize modifier types
----------------------------------------------------------------------------------------------------------------------------------------------------------


-- Initial step here to normalize the raw output
	--restricts to only those notes that  fell within 30 days of the start/indextdate if the input notes were note already pre-filtered by dates


		drop table if exists hm5hh32znlp_output_30day;
		CREATE TABLE hm5hh32znlp_output_30day
			  AS
SELECT
distinct 
			coh.person_id
			, coh.start_date
			, note.note_id
			, note.note_date
			, nlp.prone_status
			, nlp.phrase_modifier
			, nlp.spanstart
			, nlp.spanend
		
FROM
`som-nero-nigam-starr`.covid139_nlp_output  nlp 
				join 	
					`som-rit-phi-starr-prod.starr_omop_cdm5_deid_latest`.note note
					on nlp.note_id=note.note_id
				join  result_schema
					 `som-nero-nigam-starr`. coh  
					  on note.person_id=coh.person_id
				where 
						 DATE_DIFF(IF(SAFE_CAST(note.note_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(note.note_date  AS STRING)),SAFE_CAST(note.note_date  AS DATE)), IF(SAFE_CAST(coh.start_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(coh.start_date  AS STRING)),SAFE_CAST(coh.start_date  AS DATE)), DAY) <=30 
						 and DATE_DIFF(IF(SAFE_CAST(note.note_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(note.note_date  AS STRING)),SAFE_CAST(note.note_date  AS DATE)), IF(SAFE_CAST(coh.start_date  AS DATE) IS NULL,PARSE_DATE('%Y%m%d', cast(coh.start_date  AS STRING)),SAFE_CAST(coh.start_date  AS DATE)), DAY) >=0 


----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Aggregate and combine output to a patient/admission-level
----------------------------------------------------------------------------------------------------------------------------------------------------------


		drop table if exists hm5hh32zpatient_summary;
		 with all_covid_admissions as (
			select 
				person_id
				,start_date
				,end_date
			from 
			`som-nero-nigam-starr`.

			) 
		, nlp_labels_all as (
				 select person_id
				, count(*) as proneterms
				, sum(case when phrase_modifier like 'Treated%'   then 1 else 0 end) as nlp_treated_all	-- All instances of a patient being in a prone position
				, sum(case when phrase_modifier like 'Treated%' and phrase_modifier not like 'Treated:MechanicalVentilation'  then 1 else 0 end) as nlp_treated_awake --All instances of a patient in a prone position and not explicitly categorized as mechanical ventilation
				, sum(case when phrase_modifier like 'Intent%'		then 1 else 0 end) as nlp_intent
				, sum(case when phrase_modifier like 'Exclude%'	then 1 else 0 end) as nlp_exclude
				, sum(case when phrase_modifier like 'NotTreated%'	then 1 else 0 end) as nlp_nottreated
				, sum(case when phrase_modifier like 'Surgery'	then 1 else 0 end) as nlp_surgery
				, sum(case when phrase_modifier  like 'Treated:MechanicalVentilation'  then 1 else 0 end) as nlp_treated_mechanical

				/* More helpful/granular values that are possible with the additional mechanical ventilation NLP/the CSDR, not available for the OHDSI definition

						, sum(case when Phrase_Modifier  like 'Treated%' and ProneVentilation = 1  then 1 else 0 end) as Treated_during_ventilation
						, sum(case when Phrase_Modifier  like 'Treated%' and ProneVentilation = 0  then 1 else 0 end) as Treated_Outside_ventilation_window
						, sum(case when Phrase_Modifier  like 'Treated%' and ProneBeforeVentilation = 1  then 1 else 0 end) as Treated_Before_ventilation_window
						, sum(case when Phrase_Modifier  like 'Treated%' and ProneAfterVentilation = 1  then 1 else 0 end) as Treated_After_ventilation_window
						---
						, sum(case when Phrase_Modifier  like 'Intent%' and ProneVentilation = 1  then 1 else 0 end) as Intent_during_ventilation
						, sum(case when Phrase_Modifier  like 'Intent%' and ProneVentilation = 0  then 1 else 0 end) as Intent_Outside_ventilation_window
						, sum(case when Phrase_Modifier  like 'Intent%' and ProneBeforeVentilation = 1  then 1 else 0 end) as Intent_Before_ventilation_window
						, sum(case when Phrase_Modifier  like 'Intent%' and ProneAfterVentilation = 1  then 1 else 0 end) as Intent_After_ventilation_window
						---
						, sum(case when Phrase_Modifier  like 'NotTreated%' and ProneVentilation = 1  then 1 else 0 end) as NotTreated_during_ventilation
						, sum(case when Phrase_Modifier  like 'NotTreated%' and ProneVentilation = 0  then 1 else 0 end) as NotTreated_Outside_ventilation_window
						, sum(case when Phrase_Modifier  like 'NotTreated%' and ProneBeforeVentilation = 1  then 1 else 0 end) as NotTreated_Before_ventilation_window
						, sum(case when Phrase_Modifier  like 'NotTreated%' and ProneAfterVentilation = 1  then 1 else 0 end) as NotTreated_After_ventilation_window
				*/
				, sum(case when phrase_modifier is null	then 1 else 0 end) as nlp_null

				 from hm5hh32znlp_output_30day
				  group by  1 ) ,rolled_up_summary as (

					select a.*
							, case when nlp_treated_all >= 1 then 1 else 0 end as nlp_treated_all 
							, case when nlp_treated_awake >= 1 then 1 else 0 end as nlp_treated_awake 
							, case when nlp_intent >= 1 then 1 else 0 end as nlp_intent 
							, case when nlp_nottreated >= 1 then 1 else 0 end as nlp_nottreated 
							, case when proneterms >0  then 1 else 0 end as proneterms
							,  nlp_treated_all as nlp_treated_all_count
							,  nlp_treated_awake as nlp_treated_awake_count
							,  nlp_intent as nlp_intent_count
							,  nlp_exclude
							,  nlp_nottreated as nlp_nottreated_count
							,  nlp_surgery
							,  nlp_treated_mechanical
							, case when nlp_treated_mechanical >0  then 1 else 0 end as nlp_mech_flag
							,  nlp_null
					from 
						all_covid_admissions coh 
					left join 
						nlp_labels_all nlp
					on coh.person_id=nlp.person_id

			) 
			select 
				rolled_up_summary.*
				--Single label per admission
				, case when nlp_treated_awake >=1 then 'Treated'
					when nlp_intent >=1 and nlp_treated_awake =0  and nlp_nottreated >= 0 then 'Intent'  --Setting intent to override when negation occurs
					when nlp_nottreated >= 1 then 'NotTreated'
					else 'NoDocumentation'
				end as pronetreatment
				-- optional/testing a binary label, with intent included in treated
				, case when nlp_treated_awake >=1 then 'Treated' else 'NotTreated' end as pronetreatmentbinary_includeintent
				-- testing a binary label, with intent excluded as treatment
				, case when nlp_treated_awake >=1 then 'Treated' else 'NotTreated' end as pronetreatmentbinary_excludeintent	
			into hm5hh32zpatient_summary
			from 
				rolled_up_summary


				


		
		/*
		select * 
		into `som-nero-nigam-starr`.COVID139_admission_summary
		from 
		#patient_summary
		*/



----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Some Analytic/summary review queries 
----------------------------------------------------------------------------------------------------------------------------------------------------------


/* ***** ***** Total Count of admissions and patients ***** ***** */

		select count(*), count(distinct person_id) from 
		`som-nero-nigam-starr`.covid139_admission_summary



/* ***** ***** Total Count Treated ***** ***** */

		select 
		pronetreatment
		, count(distinct person_id)
		from 
				`som-nero-nigam-starr`.covid139_admission_summary
		group by pronetreatment