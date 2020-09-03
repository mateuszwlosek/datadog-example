# Datadog - Example

Example of Spring Boot [Datadog](https://www.datadoghq.com/) ready application that can be deployed in openshift and send metrics to datadog with configuration that allows investigating specific instances of an application (by pod name).

Example application was developed in [Spring Boot](https://projects.spring.io/spring-boot/).  
[Minishift](https://www.okd.io/minishift/) was used to deploy application locally for tests.

## Spring Boot application
Application uses POD_NAME environment variable to send metrics to datadog with proper id, so it is possible to investigate which pod sends particular metrics.

## Minishift
I used minishift to easily simulate production environment scenario and test case with sending metrics from every pod but with id of a pod.

## Datadog
In this example I used default spring boot metrics e.g memory usage, CPU usage etc. but I also wrote a functionality to send metrics about HTTP response codes.

## How to run the environment?

### Before you start
* Install `Docker`  
[Docker CE INSTALLATION](https://docs.docker.com/install/linux/docker-ce/ubuntu/)  

* Download `minishift`  
[Installing minishft](https://docs.okd.io/3.11/minishift/getting-started/installing.html)  
[Minishift releases](https://github.com/minishift/minishift/releases)

* Set up virtualization environment for minishift
[Setting Up the Virtualization Environment](https://docs.okd.io/3.11/minishift/getting-started/setting-up-virtualization-environment.html)

* Download `oc tools`
[oc tools](https://www.okd.io/download.html)


### Start the environment
1. Build service:  
    `docker build .`
2. Run minishift:  
    `./minishift start --show-libmachine-logs -v5`
3. Configure minishift:  
     `oc login -u system:admin`  
     `./oc adm policy add-cluster-role-to-user cluster-admin admin`  
     `./oc create is test-image-stream -n myproject`
4. Do port forwarding:  
     `oc login -u system:admin`  
     `oc project default`
     `oc get pods` - check exact name of a pod with `docker-registry` in name  
     `oc port-forward docker-registry-1-zxsxx 8089:8089`
5. Login to minishift docker registry with token from minishift console:  
     - While starting minishift, there will be an url in logs to access minishift console  
     - Login with admin username and any password  
     - Click on right upper corner icon and chose `Copy Login Command`  
     - Login command has been placed in clipboard, paste it in any text editor and extract token part from it  
     `docker login -u admin -p hp57zxkJ8jvxgtDMgDIXNDTCeBwlML_l_csut1eyYVk localhost:5000` - use that token in docker login command
5. Upload docker image:  
     `docker tag datadog-example localhost:5000/myproject/datadog-example`  
     `docker push localhost:5000/myproject/datadog-example`
6. Use uploaded docker image:  
     - Log in to minishift web console  
     - Choose `My Project` project  
     - Choose `Deploy Image`  
     - In `Image Stream Tag` choose `myproject` then `test` and `latest` (if nothing can be selected in third box, just click on it and press enter), then click `Deploy` 
7. Configure environment variables:  
    - In `test` deployment, in actions choose `Edit YAML`  
    - Modify config to contain `POD_NAME` variable  
    ```  spec:
      containers:
        - env:
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  apiVersion: v1
                  fieldPath: metadata.name
     ```
     - In `Environemnt` of test add other environment variables:  
       - DATADOG_API_KEY: has to be fetched from https://app.datadoghq.com/account/settings#api  
       - DATADOG_APPLICATION_KEY: has to be fetched from https://app.datadoghq.com/account/settings#api  
8. Check datadog metric explorer:  
     - Go to https://app.datadoghq.com/metric/explorer
![datadog metrics](https://user-images.githubusercontent.com/15820051/83904230-1c617900-a725-11ea-9b8d-47a2cc6dc4dc.png)
     
### Important endpoints (Can be executed in pod terminal with curl)
* http://localhost:8080/test-200 - Test 200 HTTP response code metric
* http://localhost:8080/test-500 - Test 500 HTTP response code metric
     
Completely reset minishift:  
     `minishift delete --force --clear-cache`  
     `minishift stop`
