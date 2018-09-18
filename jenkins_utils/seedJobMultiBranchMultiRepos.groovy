//W skrypcie MrCheckerowym trzeba odwolywac sie tak
//string(defaultValue: "${env.BRANCH_NAME}", daescription: 'Execute job on given branch', name: 'WORKING_BRANCH'),
//I bedziemy miec ladne templatki

class JobConfig{
    def URL
    String description
    def oldItemsNumKeep
    def oldItemsDaysKeep
    String jobName
    String toString(){
        return "$description:$URL:$oldItemsNumKeep:$oldItemsDaysKeep"
    }
}
String folderSource = '''
folder(':folder:') {
    description('Repository jobs for Zensus Projects')
}

'''
String dslScriptTemplate='''
multibranchPipelineJob(':folder:/:jobName:.:browser:') {
    description(":description:")

    factory {   
        workflowBranchProjectFactory {
            scriptPath('exampleCheckerAppUnderTest/pipelines/CI/Jenkinsfile_node.groovy')
        }
    }

    branchSources{
        git{
            remote(':URL:')
            includes('*')
        }
        orphanedItemStrategy {
            discardOldItems {
                numToKeep(:oldItemsNumKeep:)
                daysToKeep(:oldItemsNumKeep:)
            }
       }
    }
}
'''
public enum JOB_TYPES {
    FEATURE("tests/feature"), REGRESSION("tests/regression"), RELEASE("tests/release")
    final String folder
    private JOB_TYPES(String folder){
        this.folder = folder
    }
}

def getJobForConfig(String jobTemplate,JobConfig jobConfig,JOB_TYPES jobType){
    jobConfig['oldItemsNumKeep'] = jobConfig['oldItemsNumKeep']?:1
    jobConfig['oldItemsDaysKeep'] = jobConfig['oldItemsDaysKeep']?:1
    return jobTemplate.
            replaceAll(':description:',jobConfig['description']).
            replaceAll(':URL:',jobConfig['URL']).
            replaceAll(':oldItemsNumKeep:',jobConfig['oldItemsNumKeep']).
            replaceAll(':oldItemsDaysKeep:',jobConfig['oldItemsDaysKeep']).
            replaceAll(':jobName:',jobConfig['jobName']).
            replaceAll(':folder:',jobType.folder)
}


Map<String,JobConfig> repoJobConfigs = [:]
repoJobConfigs.put('saps',
        new JobConfig(
                URL:'https://github.com/gabrielstar/exampleChecker.git',
                description:"Sonderbereiche",
                oldItemsNumKeep:'0',
                oldItemsDaysKeep:'0',
                jobName:'saps')
)
repoJobConfigs.put('mr2019',
        new JobConfig(
                URL:'https://github.com/gabrielstar/exampleChecker.git',
                description:"Meldung Register 2019",
                oldItemsNumKeep:'0',
                oldItemsDaysKeep:'0',
                jobName:'mr2019')
)

final String mainFolder = "tests"
final List browsers = ["chrome","firefox","internet explorer"]
List dslScripts = []

node('master'){
    stage("Create Folder Structure"){
        String folderDsl
        List folders = []
        folders.add(folderSource.replaceAll(':folder:',"tests"))
        JOB_TYPES.each{
            folders.add(folderSource.replaceAll(':folder:',it.folder))
        }
        folderDsl = folders.join("\n")
        writeFile(file: 'folderStructure.groovy', text: folderDsl)
        jobDsl failOnMissingPlugin: true,  unstableOnDeprecation: true, targets: 'folderStructure.groovy'

    }
    stage("Prepare Job Configurations"){
        repoJobConfigs.each{ String repoName,JobConfig repoConfig->
            println "Working on ... " + repoConfig.toString()
            println "Generating job code: " + getJobForConfig(dslScriptTemplate, repoConfig, JOB_TYPES.FEATURE)
            browsers.each{browser->
                dslScripts.add(getJobForConfig(dslScriptTemplate, repoConfig, JOB_TYPES.FEATURE).replaceAll(':browser:',browser))
            }
        }

    }
    stage('Create Feature Jobs'){
        if(dslScripts.size()>0){

            String dslOutput =  dslScripts.join("\n")
            writeFile(file: 'dslOutput.groovy', text: dslOutput)
            jobDsl failOnMissingPlugin: true,  unstableOnDeprecation: true, targets: 'dslOutput.groovy', additionalParameters:[BOO: 'dupa']


        }
    }
    stage('Create Regression Jobs'){
    }
    stage('Create Release Jobs'){
    }
    stage('Create Views'){

    }

}