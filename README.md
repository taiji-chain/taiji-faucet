# Taiji Faucet Server

To start the server from docker-compose. 

```
cd ~/taiji-chain/light-docker
docker-compose -f docker-compose-local1.yaml up
```

To access the faucet from curl command line.

```
curl -k -X POST https://localhost:2497/faucet/000046b6312283A1a22c4efb2C7711886bB02F2b -H 'Content-Type: application/json' -d '{"currency": "taiji","amount": "1000","unit": "TAIJI"}'
```