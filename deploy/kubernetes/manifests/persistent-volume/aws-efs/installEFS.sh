
echo "Setting up persistent volume"
kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f efs/persistentvol.yml

echo "Setting up persistent volume claim"
kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f efs/persistentvol-claim.yml