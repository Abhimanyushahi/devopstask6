  
job('task6-job1') {
	description('job1')
		scm {
			github('Abhimanyushahi/devopstask6', 'master')
		}
	steps {
        	shell('''
		         sudo mkdir /home/jenkins
		         sudo cp * -rvf /home/jenkins
		     ''')
		}
		triggers {
                	scm('* * * * * ')
			}
		triggers { 
                	upstream('seed', 'SUCCESS')
               }
		
}

job('task6-job2'){
	description('job2')
		
		triggers { 
                	upstream('task6-job1', 'SUCCESS')
               }
  
		steps {
			shell('''
			sudo cd /home/jenkins
			if ls  | grep php
			then
                        echo "php file found here"
			   if sudo kubectl get deployment | grep httpd
			   then
			   echo "deployment already exit"
			   else
			   sudo kubectl create -f deployment.yml
			   fi
			POD=$(sudo kubectl get pods -l app=httpd -o jsonpath="{.items[0].metadata.name}")
			echo "Waiting"
			sleep 15
			sudo kubectl cp index.php $POD:/var/www/html
			else
			echo "webpage not present here"
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
        	status=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.100:31000/index.php)
        	if [[ $status == 200 ]]
        	then 
	        echo "Site is working fine"
         	exit 0
                else
		echo "Some Problem in site"
                exit 1
                fi
                ''')
             }
 publishers { 
	extendedEmail { 
		recipientList('131ajay0@gmail.com')
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




