scp dist/js/*.js django2:~/kob/acapp/
scp dist/css/*.css django2:~/kob/acapp/

ssh django2 'cd kob/acapp && ./rename.sh'
