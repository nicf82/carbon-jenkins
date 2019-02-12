
def sites = [
    "www.carboninternet.net",
    "service.carboninternet.net",
    "www.vandervelde.co.uk",
    "www.amboxingnewcastle.co.uk",
    "dev.flightsimcentre.com",
    "www.blueyonderairtours.com",
    "www.combat-athletics.co.uk",
    "www.funkylimos.com",
    "www.ghdangels.co.uk",
    "www.jcqualitymanagement.com"
]

folder('spider-checks') {
    description('Website spiders checking for broken links')
}

for(site in sites) {
    
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
                """--ignore-url xmlrpc.php\$ http://""" + site)
        }
        publishers {
            slackNotifier {
                // teamDomain('foo')
                // authToken('bar')
                // room('test')
                botUser(false)
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
                commitInfoChoice('NONE')
            }
        }
    }
}

job("email-sender") {
    triggers{
        bitbucketPush()
    }
	description("Email sender test and build")
	keepDependencies(false)
    disabled(false)
    concurrentBuild(false)
	scm {
		git {
            remote {
                name('origin')
                url("git@bitbucket.org:carboninternet/email-sender.git")
                credentials("a6003119-dffb-4c00-92bd-356c93e91c23")
            }
			branch("*/master")
		}
	}
	steps {
		shell("""docker run --rm --name laravel -v \$PWD:/var/www hitalos/laravel composer install
docker run --rm --name laravel -v /var/lib/jenkins/workspace/email-sender:/var/www -e LOG_CHANNEL=stderr hitalos/laravel phpunit""")
	}

}
