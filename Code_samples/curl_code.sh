# NOTE: you must set $API_KEY below using information retrieved from your IBM Cloud account (https://dataplatform.cloud.ibm.com/docs/content/wsj/analyze-data/ml-authentication.html?context=wx)

export API_KEY=<your API key>

export IAM_TOKEN=$(curl --insecure -X POST --location "https://iam.cloud.ibm.com/identity/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--header "Accept: application/json" \
--data-urlencode "grant_type=urn:ibm:params:oauth:grant-type:apikey" \
--data-urlencode "apikey=$API_KEY" | jq -r '.access_token')

# TODO:  manually define and pass values to be scored below

curl --location "https://us-south.ml.cloud.ibm.com/ml/v4/deployments/3553aed4-7284-4a5f-a70f-1c12334a241e/ai_service_stream?version=2021-05-01" \
--header "Content-Type: application/json" \
--header "Accept: application/json" \
--header "Authorization: Bearer $IAM_TOKEN" \
--data "`{"messages":[{"content":"","role":""}]}`"