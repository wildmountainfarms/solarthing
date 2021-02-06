## Installing Grafana Docker
Copy this directory to another location on your computer. 

Example:
```shell
cd /opt
sudo mkdir containers
cd containers
sudo cp -r /opt/solarthing/config_templates/docker/grafana .
```
Then run the setup
```shell
cd grafana
sudo ./setup.sh
```
Then start up the container
```shell
sudo docker-compose up -d
```
Then install the GraphQL plugin:
```shell
sudo docker-compose exec grafana bash
# You should now be in a shell inside the docker container
grafana-cli plugins install fifemon-graphql-datasource
exit # get out of the docker container's shell
```
Now restart the container
```shell
sudo docker-compose up -d --force-recreate
```
