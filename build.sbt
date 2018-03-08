name := """BB8"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
lazy val myProject = (project in file("."))
 .enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
         
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources) 
 
EclipseKeys.preTasks := Seq(compile in Compile)    

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
libraryDependencies ++=Seq( "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
                             "org.docx4j" % "docx4j" % "2.7.1",
                             "com.lowagie" % "itext" % "4.2.0",
                             "org.apache.poi" % "poi-scratchpad" % "3.10-FINAL",
                             "org.apache.poi" % "poi" % "3.13",
                             "javax.mail" % "mail" % "1.4.5",
                             "com.itextpdf" % "itextpdf" % "5.0.6",
                             "com.itextpdf.tool" % "xmlworker" % "5.5.6",
                             "org.apache.poi" % "poi-ooxml" % "3.13",
                             "org.apache.commons" % "commons-csv" % "1.2",
                             "joda-time" % "joda-time" % "2.9.2",
                             "org.webjars" % "select2" % "3.5.2-1",
                             "com.ibm.icu" % "icu4j" % "56.1",
                             "net.sf.biweekly" % "biweekly" % "0.3.0",
                             "com.atlassian.commonmark" % "commonmark" % "0.8.0",
                             "one.util" % "streamex" % "0.6.4",
                             "com.googlecode.json-simple" % "json-simple" % "1.1",
                             "com.google.api-client" % "google-api-client" % "1.22.0",
							 "com.google.apis" % "google-api-services-calendar" % "v3-rev235-1.22.0",
							 "com.google.oauth-client" % "google-oauth-client-jetty" % "1.22.0",
							 "com.google.apis" % "google-api-services-drive" % "v3-rev61-1.22.0"
                            )                           
                          