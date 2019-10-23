_This is a single pod to have shell access to the kubernetes cluster in order to poke around internally._

Install this pod
```kubectl apply -f bastion.yaml```

Access the pod:
```kubectl exec -it bastion-pod -- /bin/sh```
