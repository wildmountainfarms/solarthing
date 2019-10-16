# Quick Start With Outback MATE
Clone the repo
```
git clone https://github.com/wildmountainfarms/solarthing
cd solarthing/program
```
Copy some template config files
```
cp ../config_templates/mate_io.json config/
cp ../influxdb_template.json config/influxdb.json
```
Edit `influxdb.json`
```
# This opens it in vi or vim. You can also use your text editor of choice
vi influxdb.json
```
Run the program for the first time
```
java -jar solarthing.jar mate --unique 60 --io config/mate_io.json --database config/influxdb.json
```
