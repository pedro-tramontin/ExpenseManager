name := "ExpenseManager"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
	javaJdbc,
	javaEbean,
	cache,
	"mysql" % "mysql-connector-java" % "5.1.21",
	"com.google.inject" % "guice" % "3.0",
	"org.apache.poi" % "poi" % "3.12",
	"org.apache.poi" % "poi-ooxml" % "3.12",
	"net.sf.opencsv" % "opencsv" % "2.3"
)     

play.Project.playJavaSettings
