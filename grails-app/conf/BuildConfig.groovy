grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
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
            excludes 'jmxtools', 'jmxri', 'jms', 'slf4j-log4j12'
        }
        compile('net.jradius:jradius-dictionary:1.1.4') {
            // uses log4j 1.2.15 which has bad dependencies
            // just exclude log4j?
            excludes 'jmxtools', 'jmxri', 'jms', 'slf4j-log4j12'
        }
        compile('net.jradius:jradius-extended:1.1.4') {
            // uses log4j 1.2.15 which has bad dependencies
            // just exclude log4j?
            excludes 'jmxtools', 'jmxri', 'jms', 'slf4j-log4j12'
        }
    }

    plugins {
        build ':release:2.2.1', ':rest-client-builder:1.0.3', {
            export = false
        }

        compile ":spring-security-core:1.2.4"

        runtime ":hibernate:$grailsVersion", {
            export = false
        }
    }
}
