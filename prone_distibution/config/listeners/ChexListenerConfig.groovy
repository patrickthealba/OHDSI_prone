package listeners

import gov.va.vinci.leo.context.types.TermContext


import gov.va.vinci.listeners.ChexListener
import gov.va.vinci.leo.tools.LeoUtils
import gov.va.vinci.vent.types.Prone_Pattern
import gov.va.vinci.vent.types.Vent_Pattern


String timeStamp = LeoUtils.getTimestampDateDotTime().replaceAll("[.]", "_")


String chexSchema = "chex"  //
String chexSuffix = "_prone_complete_timeline"// Change the suffix for each run, otherwise the data WILL BE OVERWRITTEN!
def chexTypes= [Prone_Pattern.getCanonicalName()] // when blank, SimanListener outputs all annotations


boolean chexOverwrite = true
String chexDocumentTextSelectQuery = ""
String chexColumnPrefix = "["
String chexColumnSuffix ="]"
int batchSize = 1000
String url = ""
String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"



listener = ChexListener.newChexListener(
        driver,
        url,
        chexDocumentTextSelectQuery,
        chexSchema,
        chexSuffix,
        chexColumnPrefix,
        chexColumnSuffix,
        chexTypes,
        batchSize,
        chexOverwrite)


