grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenCentral()
        mavenRepo "http://dev.coova.org/mvn/"
        // for ehcache 2.2.0 dependency problem (GRAILS problem?)
        // http://jira.terracotta.org/jira/browse/EHC-811
        mavenRepo "http://www.terracotta.org/download/reflector/releases"
    }
    dependencies {
        compile('net.jradius:jradius-core:1.1.4') {
            // uses log4j 1.2.15 which has bad dependencies
            // just exclude log4j?
            excludes 'jmxtools', 'jmxri', 'jms'
        }
        compile('net.jradius:jradius-dictionary:1.1.4') {
            // uses log4j 1.2.15 which has bad dependencies
            // just exclude log4j?
            excludes 'jmxtools', 'jmxri', 'jms'
        }
        compile('net.jradius:jradius-extended:1.1.4') {
            // uses log4j 1.2.15 which has bad dependencies
            // just exclude log4j?
            excludes 'jmxtools', 'jmxri', 'jms'
        }
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:1.0.0.RC3") {
            export = false
        }
    }
}
