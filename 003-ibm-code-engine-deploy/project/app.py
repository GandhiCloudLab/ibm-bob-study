from flask import Flask, render_template
import os

app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/health')
def health():
    return {'status': 'healthy', 'message': 'Application is running'}, 200

@app.route('/api/info')
def info():
    return {
        'app_name': 'Simple Python Web App',
        'version': '1.0.0',
        'environment': os.getenv('ENVIRONMENT', 'production')
    }, 200

if __name__ == '__main__':
    port = int(os.getenv('PORT', 8080))
    app.run(host='0.0.0.0', port=port, debug=False)

# Made with Bob
