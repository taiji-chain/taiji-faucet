# taiji-faucet
A site that help testers to acquire testing taiji coins on testnet and this is only the API part. There is a single page application what will be deployed with the light-router running on the portal host. 

To deploy the API to the Kubernetes cluster, login to the sandbox which is the master node of the Kubernetes cluster. Make sure that you have a folder named light-chain and clone the light-kube to the light-chain folder. 

If the directory exists, please sync the folder with `git pull origin master`

If this is the first time to deploy, we need to create the secret by running the command in tiaji-faucet folder. 

```
./create-secrets.sh
```

Once the secret is created, run the following command to start the service.

```
kubectl create -f deployment.yml
```

If the taiji-faucet api is updated, then we need to create a new image and upload to the docker hub. 

```
mvn clean install
./build.sh 1.0.2
```
