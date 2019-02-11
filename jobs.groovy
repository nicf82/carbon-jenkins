
def sites = [
    "www.carboninternet.net",
    "service.carboninternet.net",
    "www.vandervelde.co.uk",
    "www.amboxingnewcastle.co.uk",
    "www.flightsimcentre.com",
    "www.blueyonderairtours.com",
    "www.combat-athletics.co.uk",
    "www.funkylimos.com",
    "www.ghdangels.co.uk",
    "www.jcqualitymanagement.com"
]

folder('spider-checks') {
    description('Websites')
}

for(site in sites){
    
    job('spider-checks/' + site) {
        triggers {
            cron('H H * * *')
        }
        description("Spider link checker: " + site)
        keepDependencies(false)
        disabled(false)
        concurrentBuild(false)
        steps {
            shell(
                """docker run --rm carboninternet/linkchecker """ + 
                """--threads 1 --recursion-level 3 """ + 
                """--ignore-url xmlrpc.php\$ """ + site)
        }
        publishers {
            slackNotifier {
                // teamDomain('foo')
                // authToken('bar')
                // room('test')
                startNotification(false)
                notifySuccess(false)
                notifyAborted(true)
                notifyNotBuilt(true)
                notifyFailure(true)
                notifyUnstable(true)
                notifyBackToNormal(true)
                notifyRegression(false)
                notifyRepeatedFailure(true)
                //includeTestSummary(false)
                // includeCustomMessage(true)
                // customMessage('Hello!')
                // buildServerUrl(null)
                // sendAs(null)
                // commitInfoChoice('NONE')
            }
        }
    }
}