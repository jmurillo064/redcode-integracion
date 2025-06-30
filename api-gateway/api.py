from flask import Flask, jsonify

app = Flask(__name__)

@app.route("/api/usuarios")
def usuarios():
    return jsonify([{"id": 1, "nombre": "Jorge"}])

@app.route("/api/clientes")
def clientes():
    return jsonify([{"id": 99, "empresa": "RedCode"}])

if __name__ == "__main__":
    app.run(port=8081)
