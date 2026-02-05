# Python Web App on IBM Code Engine

## 🎯 Objective

This project demonstrates a complete cloud-native deployment workflow by creating a simple Python Flask web application, containerizing it using Podman, pushing the container image to IBM Container Registry, and deploying it on IBM Code Engine. The goal is to showcase modern DevOps practices and IBM Cloud services integration.

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     Development Environment                      │
│                                                                   │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐  │
│  │   app.py     │      │ Dockerfile   │      │requirements  │  │
│  │  (Flask App) │      │              │      │    .txt      │  │
│  └──────────────┘      └──────────────┘      └──────────────┘  │
│         │                      │                      │          │
│         └──────────────────────┴──────────────────────┘          │
│                                │                                 │
└────────────────────────────────┼─────────────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │   Podman Build         │
                    │   (Container Image)    │
                    └────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    IBM Container Registry                        │
│                    (us.icr.io)                                   │
│                                                                   │
│  Namespace: cr-itz-3uehbja7                                      │
│  Image: python-web-app:latest                                    │
│                                                                   │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                      IBM Code Engine                             │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  Project: python-web-app-project                       │     │
│  │  Resource Group: itz-wxo-697b4e2bf2289c92dfa7cf        │     │
│  │                                                         │     │
│  │  ┌──────────────────────────────────────────────┐     │     │
│  │  │  Application: python-web-app                 │     │     │
│  │  │  Port: 8080                                  │     │     │
│  │  │  Auto-scaling: Enabled                       │     │     │
│  │  └──────────────────────────────────────────────┘     │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                   │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │   Public Endpoint      │
                    │   (HTTPS URL)          │
                    └────────────────────────┘
                                 │
                                 ▼
                         ┌──────────────┐
                         │   End Users  │
                         └──────────────┘
```

## 🛠️ Tech Stack

### Application Layer
- **Language**: Python 3.11
- **Framework**: Flask 3.0.0
- **Web Server**: Werkzeug 3.0.1

### Containerization
- **Container Runtime**: Podman
- **Base Image**: python:3.11-slim
- **Container Registry**: IBM Container Registry (ICR)

### Cloud Infrastructure
- **Cloud Provider**: IBM Cloud
- **Compute Service**: IBM Code Engine
- **Container Registry**: IBM Container Registry (us.icr.io)
- **Region**: us-south
- **Resource Group**: itz-wxo-697b4e2bf2289c92dfa7cf

### DevOps Tools
- **CLI**: IBM Cloud CLI
- **Plugins**: 
  - code-engine
  - container-registry
- **Authentication**: IBM Cloud API Key

## 📁 Project Structure

```
.
├── app.py              # Flask application with REST endpoints
├── Dockerfile          # Container image definition
├── requirements.txt    # Python dependencies
└── README.md          # Project documentation
```

## 🚀 Application Endpoints

### Root Endpoint
- **URL**: `/`
- **Method**: GET
- **Response**: JSON with application status and welcome message

```json
{
  "message": "Hello from IBM Code Engine!",
  "status": "running",
  "app": "Simple Python Web App"
}
```

### Health Check Endpoint
- **URL**: `/health`
- **Method**: GET
- **Response**: JSON with health status

```json
{
  "status": "healthy"
}
```

## 🔧 Deployment Details

### Container Registry
- **Registry**: us.icr.io
- **Namespace**: cr-itz-3uehbja7
- **Image**: python-web-app:latest
- **Full Image Path**: `us.icr.io/cr-itz-3uehbja7/python-web-app:latest`

### Code Engine Configuration
- **Project Name**: python-web-app-project
- **Application Name**: python-web-app
- **Port**: 8080
- **Registry Secret**: icr-secret
- **Auto-scaling**: Enabled (managed by Code Engine)

## 📝 Deployment Steps

1. **Build Container Image**
   ```bash
   podman build -t python-web-app:latest .
   ```

2. **Authenticate with IBM Cloud**
   ```bash
   ibmcloud login --apikey <API_KEY> -r us-south
   ibmcloud target -g itz-wxo-697b4e2bf2289c92dfa7cf
   ```

3. **Login to Container Registry**
   ```bash
   ibmcloud cr login
   ```

4. **Tag and Push Image**
   ```bash
   podman tag localhost/python-web-app:latest us.icr.io/cr-itz-3uehbja7/python-web-app:latest
   podman push us.icr.io/cr-itz-3uehbja7/python-web-app:latest
   ```

5. **Create Code Engine Project**
   ```bash
   ibmcloud ce project create --name python-web-app-project
   ```

6. **Create Registry Secret**
   ```bash
   ibmcloud ce registry create --name icr-secret --server us.icr.io --username iamapikey --password <API_KEY>
   ```

7. **Deploy Application**
   ```bash
   ibmcloud ce application create --name python-web-app --image us.icr.io/cr-itz-3uehbja7/python-web-app:latest --registry-secret icr-secret --port 8080
   ```

## 🌐 Access the Application

The application is deployed and accessible at:
**https://python-web-app.25vld93gsqo2.us-south.codeengine.appdomain.cloud**

## ✨ Key Features

- **Serverless Deployment**: Runs on IBM Code Engine with automatic scaling
- **Container-based**: Fully containerized using Podman
- **Cloud-native**: Leverages IBM Cloud services for registry and compute
- **RESTful API**: Simple JSON-based REST endpoints
- **Health Monitoring**: Built-in health check endpoint
- **Secure**: Uses IBM Cloud API key authentication and registry secrets

## 🔐 Security

- API keys are used for authentication with IBM Cloud services
- Container registry access is secured with registry secrets
- Application runs in isolated Code Engine environment
- HTTPS endpoint provided by default

## 📊 Benefits of This Architecture

1. **Scalability**: Code Engine automatically scales based on demand
2. **Cost-Effective**: Pay only for actual usage (serverless model)
3. **Portability**: Container-based deployment ensures consistency
4. **Managed Infrastructure**: No server management required
5. **High Availability**: Built-in redundancy and load balancing
6. **Fast Deployment**: Quick iteration and deployment cycles

## 🎓 Learning Outcomes

This project demonstrates:
- Building Python web applications with Flask
- Containerizing applications using Podman
- Working with IBM Container Registry
- Deploying serverless applications on IBM Code Engine
- Managing cloud resources using IBM Cloud CLI
- Implementing DevOps best practices

## 📄 License

This is a demonstration project for learning purposes.

---

**Built with ❤️ using IBM Cloud Services**