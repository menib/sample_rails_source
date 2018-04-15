#!/usr/bin/env groovy

try {
    node('gp2') {
      stage('Checkout') {
          sh 'sudo rm -rf *'
          checkout scm
      }
      stage('Pack & Archive') {
          parallel (
            client: {
              dir("website") {
                sh 'touch website.tar.gz && tar -zcvf website.tar.gz --exclude=website.tar.gz .'
                archiveArtifacts artifacts: "website.tar.gz", fingerprint: true
              }
            },
            server: {
              dir("db_backups") {
                sh 'touch db_backups.tar.gz && tar -zcvf db_backups.tar.gz --exclude=db_backups.tar.gz .'
                archiveArtifacts artifacts: "db_backups.tar.gz", fingerprint: true
              }
            }
          )
        
      }
      stage('Promote to production') {
          parallel (
            client: {
              dir("website") {
                sh "aws s3 cp website.tar.gz s3://ngdevbox/applications/rails-website/production/website.tar.gz"
              }
            },
            server: {
              dir("db_backups") {
                sh "aws s3 cp db_backups.tar.gz s3://ngdevbox/applications/mysql/production/db_backups.tar.gz"
              }
            }
          )
      }
  }
} catch (err)
{
  currentBuild.result = "FAILURE"
  throw err
}
