/**
 * <pre>
 *  
 *  0.- Get the parameter "browser" and "browser_version" of current job
 *  1.- Get the parameter multibrowser of the childs jobs of the current job (a multiproject job)
 *  2.- For every child job with multibrowser==true: 
 *		- change values of  "webdriver.browser" and "webdriver.browser_version" with the respectives "browser" and "browser_version"
 *  
 *  
 *  </pre>
 */
import hudson.model.*
import hudson.model.labels.*
import com.tikal.jenkins.plugins.multijob.*

////////////////////////////////////////////////////////////
///// Métodos //////////////////////////////////////////////
////////////////////////////////////////////////////////////
// Método para setear parámetros a un job
def setJobParameter(job, key, value, desc) {
  newParam = new StringParameterDefinition(key, value, desc)
  paramDef = job.getProperty(ParametersDefinitionProperty.class)
  if (paramDef == null) {
  	newArrList = new ArrayList<ParameterDefinition>(1)
    newArrList.add(newParam)
	newParamDef = new ParametersDefinitionProperty(newArrList)
	job.addProperty(newParamDef)
  } else {
	// Parameters exist! We should check if this one exists already!
    found = paramDef.parameterDefinitions.find{ it.name == key }
    if (found == null) {
    	paramDef.parameterDefinitions.add(newParam)
	}
  }
}

def changeBrowser(subJob, browser_value, browserVersion_value) {
  	def browser ="webdriver.browser"
	def browserVersion = "webdriver.browser_version"
  
	setJobParameter(subJob, browser, browser_value, "") 
	println "changedBrowser to " + browser_value
  
  	setJobParameter(subJob, browserVersion, browserVersion_value, "")
  	println "changedBrowserVersion to " + browserVersion_value 	
}  

def changeLabel(job, label) {
	job.assignedLabel = new LabelAtom(label) 
   	println "changedLabel to " + label
}  

def getLabelForExecution(browser_value, browserVersion_value) {
  def FirefoxLabel="FIREFOX"
  def IExplorer8Label="IExplorer8"
  def IExplorer11Label="IExplorer11"
  def IExplorer11WithCompLabel="IExplorer11WithCompibility"
  
  
  def answer= FirefoxLabel;
  if (browser_value=="IEXPLORER"){
    answer= IExplorer8Label;
    if (browserVersion_value=="11"){
   		answer= IExplorer11Label;
  	} else if (browserVersion_value=="11-WithCompibility"){
    	answer= IExplorer11WithCompLabel;
  	}
  }
  
  return answer;
}  

def changeBrowserl(job, label) {
	job.assignedLabel = new LabelAtom(label) 
}  

////////////////////////////////////////////////////////////
println " "
println "change-child-job-label-by-browser-version START"  
//Obtenermos valor de los par�metros

def multibrowser="multibrowser"
def multibrowser_value=true
def browser ="browser"
def browserVersion = "browser_version"

def resolver = build.buildVariableResolver
def browser_value = resolver.resolve(browser)
def browserVersion_value = resolver.resolve(browserVersion)

def mainJobName = build.getProject().asItem().getFullName()
def job = hudson.model.Hudson.instance.getItemByFullName(mainJobName)  

def labelExecution=getLabelForExecution(browser_value, browserVersion_value);


def subs = job.getDownstreamProjects()
for (s in subs) {
  def subJob = hudson.model.Hudson.instance.getItemByFullName(s.getFullName())  
  def parameters = subJob.actions.find{ it instanceof ParametersDefinitionProperty }?.parameterDefinitions
  parameters.each {
    if (it.name == multibrowser && it.defaultParameterValue.value == multibrowser_value){
      println "For job: "+ s.getFullName() 
      changeLabel(subJob, labelExecution);     
      changeBrowser(subJob, browser_value, browserVersion_value);      
    }
      
  }
}

println "change-child-job-label-by-browser-version END."
println " "
