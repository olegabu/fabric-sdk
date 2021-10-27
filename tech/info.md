#### Use Fabric Java SDK to enable client applications to interact with a TLS-enabled Hyperledger Fabric Network
https://developer.ibm.com/tutorials/hyperledger-fabric-java-sdk-for-tls-enabled-fabric-network/

#### Cloud/Kubernetes
https://github.com/IBM/cloud-native-starter
https://istio.io/latest/docs/tasks/traffic-management/
https://github.com/IBM/kar

https://github.com/orgs/IBM/repositories?q=blockchai&type=&language=&sort=
https://github.com/IBM/blockchain-network-on-kubernetes



#### Baeldung
https://www.baeldung.com/java-bouncy-castle
https://www.baeldung.com/java-read-pem-file-keys
https://www.baeldung.com/cs/category/security


Kuber\Helm

Helm
https://helm.sh/docs/chart_template_guide/accessing_files/

Helm 3 — Mapping a directory of files into a container
https://itnext.io/helm-3-mapping-a-directory-of-files-into-a-container-ed6c54372df8


Init-container  
https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-initialization/
https://kubernetes.io/docs/tasks/access-application-cluster/communicate-containers-same-pod-shared-volume/
https://stackoverflow.com/questions/54181419/make-large-static-data-files-available-to-kubernetes-pods

Templating:  
        https://stackoverflow.com/questions/57219325/how-to-create-a-config-map-from-a-file-in-helm  
    Simplify Kubernetes deployments with Helm (Part 3) – Creating configmaps and secrets  
        https://www.nclouds.com/blog/simplify-kubernetes-deployments-helm-part-3-creating-configmaps-secrets/  
    multiple Secrets with Helm template and unifying value access with ConfigMaps.    
        https://medium.com/bootlegsoft/kubernetes-multiple-secrets-with-helm-template-and-unifying-value-access-with-configmaps-e7b6027cd76d  

USE CONFIGMAP FOR SCRIPTS  
https://suraj.io/post/use-configmap-for-scripts/


Mounting a file with Helm into a running pod  
https://emmenko.org/notes/mounting-a-file-with-helm-into-a-kubernetes-pod-using-a-config-map

Copy Files  
https://emmenko.org/notes/mounting-a-file-with-helm-into-a-kubernetes-pod-using-a-config-map
https://github.com/helm/helm/issues/3276
https://dwdraju.medium.com/copy-files-folders-to-and-from-kubernetes-pod-s-all-cases-d4a5749111b7

After a little research...  
TLDR:- Massive strings in ConfigMap templates is a fairly common pattern(/problem)

Looking at these suggests there are use cases for injecting files as value strings:

https://github.com/kubernetes/charts/blob/master/stable/mariadb/templates/configmap.yaml
https://github.com/kubernetes/charts/blob/58c6555618ee5c1bfb51acc1c133497d28b97d61/stable/uchiwa/templates/configmap.yaml
https://github.com/kubernetes/charts/blob/master/stable/prometheus/templates/server-configmap.yaml
Also looking at these suggests there are use cases for templating in non kubernetes files (out of scope for this proposal):

https://github.com/kubernetes/charts/blob/master/stable/openvpn/templates/config-openvpn.yaml
https://github.com/kubernetes/charts/blob/master/stable/jenkins/templates/config.yaml
https://github.com/kubernetes/charts/blob/master/stable/linkerd/templates/config.yaml
So I think this is still a valid proposal.


# Kuber blog
## DNS
https://kubernetes.io/blog/2017/04/configuring-private-dns-zones-upstream-nameservers-kubernetes/
Feature request: support dns aliases for service#39792  
https://github.com/kubernetes/kubernetes/issues/39792  
Custom DNS entries for kube-dns#55  
https://github.com/kubernetes/dns/issues/55

https://kubernetes.io/docs/concepts/services-networking/service/#without-selectors

~~https://kubernetes.io/docs/tasks/administer-cluster/dns-custom-nameservers/~~

Exposing an External IP Address to Access an Application in a Cluster  
https://kubernetes.io/docs/tutorials/stateless-application/expose-external-ip-address/


Reloader in Kubernetes  
https://rtfm.co.ua/en/kubernetes-configmap-and-secrets-data-auto-reload-in-pods/

Helm: reusable chart — named templates, and a generic chart for multiple applications  
https://dev.to/setevoy/helm-reusable-chart-named-templates-and-a-generic-chart-for-multiple-applications-1i8k


Fabric in Kubernetes
https://www.udemy.com/course/hyperledger-fabric-on-kubernetes-complete-guide/ 
    IBM  
    https://www.hyperledger.org/blog/2018/11/08/deploying-hyperledger-fabric-on-kubernetes
    
    Deploy for fabric 1.x  
    http://www.think-foundry.com/deploy-hyperledger-fabric-on-kubernetes-part-1/

    https://github.com/hyfen-nl/PIVT
    
CKAD exam  
https://www.cncf.io/certification/ckad/