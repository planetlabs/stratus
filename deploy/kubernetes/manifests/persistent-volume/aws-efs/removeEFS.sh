
echo "Setting up persistent volume"
kubectl --context=$K_CONTEXT --namespace=$K_CONTEXT delete -f efs/persistentvol.yml

echo "Setting up persistent volume claim"
kubectl --context=$K_CONTEXT --namespace=$K_CONTEXT delete -f efs/persistentvol-claim.yml