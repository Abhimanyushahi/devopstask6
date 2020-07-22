job('task6-job1') {
	description('job1')
		scm {
			github('Abhimanyushahi/devopstask6', 'master')
		}
	steps {
        	shell("sudo cp . * -rvf /home/jenkins")
		}
		triggers {
                	scm('* * * * * ')
			}
		triggers { 
                	upstream('Admin job(seed)', 'SUCCESS')
               }
}

job('task6-job2'){
	description('job2')
		scm {
			github('Abhimanyushahi/devopstask6', 'master')
		}
		triggers { 
                	upstream('task6-job1', 'SUCCESS')
               }
  
		steps {
			shell('''
			if ls /home/jenkins | grep php
			then
			if kebectl get deployment --selector "app in httpd" |grep httpd-web
			then
			kubectl apply - deployment.yml
			else
			kubectl create -f deployment.yml
			fi
			POD=$(kubectl get pod -l app=httpd -o jsonpath=".items[0].metadata.name}")
			kubectl cp /home/jenkins/index.php ${POD}:/var/www/html
			fi
			''')
			}
}

job('task6-job3'){
	description('job3')
		triggers { 
                	upstream('task6-job2', 'SUCCESS')
                         }
	steps {
        	shell('''
        	status=$(curl -o /dev/null -s -w "%(http_code)" http://'172.17.0.2:30002)
        	if [[ $status == 200 ]]
        	then 
         	exit 0
                else
                exit 1
                fi
                ''')
             }
  publishers { 
	extendedEmail { 
		recipientList('abhimanyushahi88@gmail.com')
		defaultSubject('job status')
			attachBuildLog(attachBuildLog=true)
		defaultContent('status Report')
		contentType('text/html')
		triggers {
			always {
				subject('build status')
				content('body')
				sendTo {
					developers()
					recipientList()
					}
				}
			}
		}
	}
}











 

