/*** BEGIN META {
  "name" : "change_project_svn",
  "comment" : "
    <ul>
    	<li>url para trunk (SNANSHOT) https://.../svnArch/trunk/$test_project</li>
	<li>url para versiones https://.../svnArch/tags/$buildVersion/$test_project </li>
  </li></ul>
  Parámetros (con ejemplo):
	svnUrlBase=https://.../svnArch/
	test_project=automated-test/automated-test-demo -> proyecto de Jenkins donde nos conectaremos al SCM
	svn_build_project=demo-main -> nombre del módulo en el SCM
	svnRootFolder=branches, tags o trunk
	svnFolderType=fwkabs, prdabs o tagabs",
  "parameters" : [$svn_project],
  "core": "1.600",
  "authors" : [
    { name : "Sergio Sacristán" }
  ]
} END META**/ 
import hudson.*

def SNAPSHOT_VERSION = "SNAPSHOT"
def TRUNK="trunk"
def VERSION_TAG="tags"
def VERSION_BRANCH="branches"
  
def svnUrlBase = "svnUrlBase"
def svnUrl = "svnUrl"
def test_project="test_project" 
def buildVersion = "buildVersion"
def svn_build_project = "svn_build_project"
def svnRootFolder = "svnRootFolder"
def svnFolderType = "svnFolderType"

println " " 
println "change_project_svn START"  
def thr = Thread.currentThread()
def build = thr?.executable
def svnUrlBase_value = build.getEnvVars()[svnUrlBase]
def svnUrl_value= build.getEnvVars()[svnUrl]
def test_project_value = build.getEnvVars()[test_project]
def buildVersion_value = build.getEnvVars()[buildVersion]
def svn_build_project_value = build.getEnvVars()[svn_build_project]
def svnRootFolder_value = build.getEnvVars()[svnRootFolder]
def svnFolderType_value = build.getEnvVars()[svnFolderType]

if (svnUrl_value==null ||svnUrl_value.trim().isEmpty()){
  if (!svnRootFolder_value){
  	svnRootFolder_value=TRUNK
  }  
  println "values svnUrlBase_value:"+svnUrlBase_value+ ", svnRootFolder_value:"+svnRootFolder_value + ", buildVersion_value:"+buildVersion_value+", test_project_value:"+test_project_value
  
  svnUrl_value = svnUrlBase_value + svnRootFolder_value + svnFolderType_value + test_project_value
  if (svnRootFolder_value.contains(VERSION_TAG)){
    svnUrl_value = svnUrl_value + "/" + buildVersion_value
  }else if (svnRootFolder_value.contains(VERSION_BRANCH)){  
    def versions=buildVersion_value.tokenize('.')
	def majorMinorVersionValue = versions[0]+"."+versions[1];
    svnUrl_value = svnUrl_value + "/" + majorMinorVersionValue
  }   
  println "SVN url:"+svnUrl_value
 
  //location = job.scm.locations[0]
  //println(job.name + " : " + location.credentialsId + " : " + location.remote + " : " + location.local)
} else {
  println "svnUrl definida como parámetro:"+ svnUrl_value
}

def hudsonInstance = hudson.model.Hudson.instance 
def job =  hudsonInstance.getItemByFullName(svn_build_project_value)  

// Inicialmente asignaba location.credentialsId, pero para asegurarnos mejor asigno a piñón las credenciales
def svnCredentialsId = "e771ac1f-69f8-457e-aa6e-605959b32816"
job.scm = new hudson.scm.SubversionSCM(svnUrl_value) 
job.scm.locations[0] = job.scm.locations[0].withCredentialsId(svnCredentialsId)
println("changed Subversion URL in " + job.name + " to " + svnUrl_value)
println "change_project_svn END."
println " "
