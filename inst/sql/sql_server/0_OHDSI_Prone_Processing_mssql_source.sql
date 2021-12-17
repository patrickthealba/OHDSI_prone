

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
3. Create dataframe of rolledup/aggregate values @nlp_admission_summary
	TODO:Create @outcome_cohort instead of target



*/




----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Populate Cohort using Atlas definition COVID_ID 139
----------------------------------------------------------------------------------------------------------------------------------------------------------



	Select 
		person_id
		,start_date
		,end_date   
		from 
		 @result_schema.@target_cohort




----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Process Notes
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Input query for database reader: 
	--If cohort is small enough all notes with key terms can be processed, otherwise this query is used to select 
	--All notes found 0-30 days from the start_date

	Select 
		person_id
		,note_id
		,note_text
		,note_date
		,note_title 
	From 
		@cdm_database_schema.note as note
		join  @result_schema.@target_cohort as coh
		on note.person_id=coh.person_id
	where 
		datediff(day, coh.start_date, note.note_date) <=30 
		and datediff(day,  coh.start_date, note.note_date) >=0 


-- Optional:
-- If there are more than several million notes to process within 30 days of the start_date
	-- a separate table with distinct documentids and a rowno can be created to batch the notes using the LEO batcheddatabase reader
	-- If query size is still a limitiation, notes can be prefiltered using only documents that contain key prone terms
	-- If note titles are mapped to HL7/Loin Clinical Document Ontology,  procedure/surgery notes can be more easily removed


----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Output
----------------------------------------------------------------------------------------------------------------------------------------------------------



		SELECT 
				note_id
			  ,Term
			  ,Term_Type
			  ,Prone_Status
			  ,Term_Modifier
			  ,Phrase_Modifier
			  ,Modifier_Term
			  ,Temporality_Modifier
			  ,Date_1
			  ,Date_2
			  ,Anchored_Sentence
			  ,SpanStart
			  ,SpanEnd
			  ,Snippets
		  FROM @result_schema.@nlp_raw_output



----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Restrict output to relevant patients and dates, normalize modifier types
----------------------------------------------------------------------------------------------------------------------------------------------------------


-- Initial step here to normalize the raw output
	--restricts to only those notes that  fell within 30 days of the start/indextdate if the input notes were note already pre-filtered by dates


		drop table if exists #nlp_output_30day;
		Select 	distinct 
			coh.person_id
			, coh.start_date
			, note.note_id
			, note.note_date
			, nlp.Prone_Status
			, nlp.Phrase_Modifier
			, nlp.Spanstart
			, nlp.SpanEnd
		into #nlp_output_30day
			 from 
					@result_schema.@nlp_raw_output  nlp 
				join 	
					@cdm_database_schema.note note
					on nlp.note_id=note.note_id
				join  result_schema
					 @result_schema.@target_cohort coh  
					  on note.person_id=coh.person_id
				where 
						 datediff(day, coh.start_date, note.note_date) <=30 
						 and datediff(day,  coh.start_date, note.note_date) >=0 


----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Aggregate and combine output to a patient/admission-level
----------------------------------------------------------------------------------------------------------------------------------------------------------


		drop table if exists #patient_summary;
		 with all_covid_admissions as (
			select 
				person_id
				,start_date
				,end_date
			from 
			@result_schema.@target_cohort

			) 
		, NLP_labels_all as (
				Select 
				person_id
				, count(*) as ProneTerms
				, sum(case when Phrase_Modifier like 'Treated%'   then 1 else 0 end) as NLP_Treated_all	-- All instances of a patient being in a prone position
				, sum(case when Phrase_Modifier like 'Treated%' and phrase_modifier not like 'Treated:MechanicalVentilation'  then 1 else 0 end) as NLP_Treated_awake --All instances of a patient in a prone position and not explicitly categorized as mechanical ventilation
				, sum(case when Phrase_Modifier like 'Intent%'		then 1 else 0 end) as NLP_Intent
				, sum(case when Phrase_Modifier like 'Exclude%'	then 1 else 0 end) as NLP_Exclude
				, sum(case when Phrase_Modifier like 'NotTreated%'	then 1 else 0 end) as NLP_NotTreated
				, sum(case when Phrase_Modifier like 'Surgery'	then 1 else 0 end) as NLP_surgery
				, sum(case when Phrase_Modifier  like 'Treated:MechanicalVentilation'  then 1 else 0 end) as NLP_Treated_Mechanical

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
				, sum(case when Phrase_Modifier is null	then 1 else 0 end) as NLP_Null

				From 
					#nlp_output_30day
				 group by person_id
		 ) ,rolled_up_summary as (

					Select a.*
							, case when NLP_Treated_all >= 1 then 1 else 0 end as NLP_Treated_all 
							, case when NLP_Treated_awake >= 1 then 1 else 0 end as NLP_Treated_awake 
							, case when NLP_Intent >= 1 then 1 else 0 end as NLP_Intent 
							, case when NLP_NotTreated >= 1 then 1 else 0 end as NLP_NotTreated 
							, case when ProneTerms >0  then 1 else 0 end as ProneTerms
							,  NLP_Treated_all as NLP_Treated_all_count
							,  NLP_Treated_awake as NLP_Treated_awake_count
							,  NLP_Intent as NLP_Intent_count
							,  NLP_Exclude
							,  NLP_NotTreated as NLP_NotTreated_count
							,  NLP_surgery
							,  NLP_Treated_Mechanical
							, case when NLP_Treated_Mechanical >0  then 1 else 0 end as NLP_mech_flag
							,  NLP_Null
					from 
						all_covid_admissions coh 
					left join 
						NLP_labels_all nlp
					on coh.person_id=nlp.person_id

			) 
			Select 
				rolled_up_summary.*
				--Single label per admission
				, case when NLP_Treated_awake >=1 then 'Treated'
					when NLP_Intent >=1 and NLP_Treated_awake =0  and NLP_NotTreated >= 0 then 'Intent'  --Setting intent to override when negation occurs
					When NLP_NotTreated >= 1 then 'NotTreated'
					Else 'NoDocumentation'
				end as ProneTreatment
				-- optional/testing a binary label, with intent included in treated
				, case when NLP_Treated_awake >=1 then 'Treated' else 'NotTreated' end as ProneTreatmentBinary_IncludeIntent
				-- testing a binary label, with intent excluded as treatment
				, case when NLP_Treated_awake >=1 then 'Treated' else 'NotTreated' end as ProneTreatmentBinary_ExcludeIntent	
			into #patient_summary
			from 
				rolled_up_summary


				


		
		/*
		select * 
		into @result_schema.@nlp_admission_summary
		from 
		#patient_summary
		*/



----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Some Analytic/summary review queries 
----------------------------------------------------------------------------------------------------------------------------------------------------------


/* ***** ***** Total Count of admissions and patients ***** ***** */

		Select count(*), count(distinct person_id) from 
		@result_schema.@nlp_admission_summary



/* ***** ***** Total Count Treated ***** ***** */

		Select 
		ProneTreatment
		, count(distinct person_id)
		from 
				@result_schema.@nlp_admission_summary
		group by ProneTreatment
