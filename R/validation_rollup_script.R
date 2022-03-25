
##Dummy data, needs a patient label and NLP system labels
df1 <- data.frame(Patient=c(1,1,1,1,2,2,3,3,3,3,4,4,5,5),
                  PatientLabel=c('Treated','','','','','NotTreated','Treated','','','','Intent','','Treated',''),
                  SystemLabel=c('Intent','','','NotTreated','','NotTreated','Treated','','','','Intent','','Treated',''))
#install.packages("sqldf")
df1

##Don't have time in this meeting
library(sqldf)

sqldf("select PatientLabel, SystemLabel, count(*) as InstanceCount from df1 group by PatientLabel, SystemLabel ")

distinct_pt_labels <- sqldf("
	 with all_covid_admissions as (
			select 
				[Patient]
			from 
			df1

			) 
		, NLP_labels_all as (
				Select 
				[Patient]
				, count(*) as ProneTerms
				, sum(case when PatientLabel like 'Treated'   then 1 else 0 end) as Treated	-- All instances of a patient being in a prone position
				, sum(case when PatientLabel like 'NotTreated'   then 1 else 0 end) as NotTreated
				, sum(case when PatientLabel like 'Intent'   then 1 else 0 end) as Intent

				From 
					df1
				 group by [Patient]
		 ) ,rolled_up_summary as (

					Select distinct coh.[Patient]
							, case when Treated >= 1 then 1 else 0 end as Treated 
							, case when Intent >= 1 then 1 else 0 end as Intent 
							, case when NotTreated >= 1 then 1 else 0 end as NotTreated 
							,  Treated as Treated_Count
							,  NotTreated as NotTreated_Count
							,  Intent as Intent_count
					from 
						all_covid_admissions coh 
					left join 
						NLP_labels_all nlp
					on coh.[Patient]=nlp.[Patient]

			) 
			Select 
				rolled_up_summary.*
				--Single label per admission
				, case when Treated >=1 then 'Treated'
					when Intent >=1 and Treated =0  and NotTreated >= 0 then 'Intent'  --Setting intent to override when negation occurs
					When NotTreated >= 1 then 'NotTreated'
					Else 'NoDocumentation'
				end as ProneTreatment
		
			from 
				rolled_up_summary")


distinct_nlp_labels <- sqldf("
	 with all_covid_admissions as (
			select 
				[Patient]
			from 
			df1

			) 
		, NLP_labels_all as (
				Select 
				[Patient]
				, count(*) as ProneTerms
				, sum(case when SystemLabel like 'Treated'   then 1 else 0 end) as Treated	-- All instances of a patient being in a prone position
				, sum(case when SystemLabel like 'NotTreated'   then 1 else 0 end) as NotTreated
				, sum(case when SystemLabel like 'Intent'   then 1 else 0 end) as Intent

				From 
					df1
				 group by [Patient]
		 ) ,rolled_up_summary as (

					Select distinct coh.[Patient]
							, case when Treated >= 1 then 1 else 0 end as Treated 
							, case when Intent >= 1 then 1 else 0 end as Intent 
							, case when NotTreated >= 1 then 1 else 0 end as NotTreated 
							,  Treated as Treated_Count
							,  NotTreated as NotTreated_Count
							,  Intent as Intent_count
					from 
						all_covid_admissions coh 
					left join 
						NLP_labels_all nlp
					on coh.[Patient]=nlp.[Patient]

			) 
			Select 
				rolled_up_summary.*
				--Single label per admission
				, case when Treated >=1 then 'Treated'
					when Intent >=1 and Treated =0  and NotTreated >= 0 then 'Intent'  --Setting intent to override when negation occurs
					When NotTreated >= 1 then 'NotTreated'
					Else 'NoDocumentation'
				end as NLP_ProneTreatment
		
			from 
				rolled_up_summary")


distinct_nlp_labels

distinct_pt_labels

jointdataset <- merge(distinct_pt_labels[ , c("Patient", "ProneTreatment")], distinct_nlp_labels[ , c("Patient", "NLP_ProneTreatment")], by = 'Patient', all.x= TRUE)


jointdataset


## Confusion Matrix 

#install required packages
install.packages('gmodels')
#import required library 
library(gmodels)

#Computes the crosstable calculations
CrossTable(jointdataset["NLP_ProneTreatment"],jointdataset["ProneTreatment"])
#Insatll required packages
install.packages('caret')
library(caret)
expected_value <- factor(jointdataset["ProneTreatment"])
predicted_value <- factor(jointdataset["NLP_ProneTreatment"])

##%
example <- confusionMatrix(expected_value, predicted_value)
