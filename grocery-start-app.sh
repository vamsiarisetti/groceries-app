cd /Users/vamsiarisetti/MyDisk/MyCode/groceries-app/
load_date=`date +%Y%m%d`
echo $load_date > log_$load_date.txt
/opt/homebrew/bin/mvn -version >> log_$load_date.txt 2>&1
/opt/homebrew/bin/mvn spring-boot:run >> log_$load_date.txt
echo "Run Successful..." >> log_$load_date.txt