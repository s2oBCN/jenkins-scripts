/**
 * <pre>
 *  
 *  Template to modify in batch mode jobs configuration
 *  1.- Get the matching jobs
 *  2.- Process job changes 
 *
 * Additional functions:
 *		- setJobParameter -> add/modify an existing parameter
 *  	- ...
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

def processJob(job) {
  println " "
  println "    ---------------------"
  println "Start Changing job:"+ job.getFullName()
  
  // Call to some function that will change the job configuration
  println "End Changing job:"+ job.getFullName()
  println "    ---------------------"
  println " "
}

// 1.- Get the matching jobs
def changeJobChildren(items) {
  for (item in items) {    
    if (item.class.canonicalName != 'com.cloudbees.hudson.plugins.folder.Folder') {
		println(item.name)
      	if (item.name.contains("-cn-")){
        	processJob(item)
      	}
    } else {
		changeJobChildren(((com.cloudbees.hudson.plugins.folder.Folder) item).getItems())
    }
  }
}

////////////////////////////////////////////////////////////
println " "
println "changeJobChildren START"  
changeJobChildren(Hudson.instance.items)

println "changeJobChildren END."
println " "
