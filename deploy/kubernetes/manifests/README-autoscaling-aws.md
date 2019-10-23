# AWS
Instructions from https://github.com/kubernetes/autoscaler/blob/master/cluster-autoscaler/cloudprovider/aws/README.md. Note this setup uses what is described in the doc as "Auto-Discovery Setup"

## Setup IAM roles
Add permissions to your existing kubernetes worker role 

IAM -> Roles -> nodes.stratus.k8s.local

```
                "autoscaling:DescribeAutoScalingInstances",
                "autoscaling:SetDesiredCapacity",
                "autoscaling:DescribeTags",
                "autoscaling:DescribeLaunchConfigurations",
                "autoscaling:DescribeAutoScalingGroups",
                "autoscaling:TerminateInstanceInAutoScalingGroup",
                
```

## Add cloudLabels to auto-scaling/instancegroup/node group settings:
For kops: 
`kops edit ig nodes`
```
spec:
  cloudLabels:
    k8s.io/cluster-autoscaler/enabled: ""
    kubernetes.io/cluster/stratus.k8s.local: ""
```

## Deploy AWS cluster-autoscaler:

`kubectl apply -f cluster-autoscaler/cluster-autoscaler-autodiscover.yaml`
Note that the node-group-auto-doscvery tags shold match the `cloudLabels` in the instance group; e.g., 
```
spec:
  template:
    spec: 
      serviceAccountName: cluster-autoscaler
      containers:
        - image: k8s.gcr.io/cluster-autoscaler:v0.6.0
          command:
            - ./cluster-autoscaler
            - --v=4
            - --expander=least-waste
            - --stderrthreshold=info
            - --cloud-provider=aws
            - --skip-nodes-with-local-storage=false
            - --node-group-auto-discovery=asg:tag=k8s.io/cluster-autoscaler/enabled,kubernetes.io/cluster/stratus.k8s.local
```

