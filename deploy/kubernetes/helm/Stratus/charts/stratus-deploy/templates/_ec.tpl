
{{- define "gce_cloudsql_proxy_container" }}
# Change [INSTANCE_CONNECTION_NAME] here to include your GCP
# project, the region of your Cloud SQL instance and the name
# of your Cloud SQL instance. The format is
# $PROJECT:$REGION:$INSTANCE
# Insert the port number used by your database.
# [START proxy_container]
- image: gcr.io/cloudsql-docker/gce-proxy:1.09
  name: cloudsql-proxy
  command: ["/cloud_sql_proxy", "--dir=/cloudsql",
            "-instances=ec-gce-test:us-east1:ec-db=tcp:5432",
            "-credential_file=/secrets/cloudsql/credentials.json"]
  volumeMounts:
    - name: cloudsql-instance-credentials
      mountPath: /secrets/cloudsql
      readOnly: true
    - name: ssl-certs
      mountPath: /etc/ssl/certs
    - name: cloudsql
      mountPath: /cloudsql
  resources:
    requests:
      cpu: 100m
# [END proxy_container]
{{- end }}

{{- define "gce_cloudsql_proxy_volumes" }}
# [START proxy volumes]
- name: cloudsql-instance-credentials
  secret:
    secretName: cloudsql-instance-credentials
- name: ssl-certs
  hostPath:
    path: /etc/ssl/certs
- name: cloudsql
  emptyDir:
# [END proxy volumes]
{{- end }}