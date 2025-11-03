# ServidorChat
Tarea de servidor

Chat Cliente-Servidor en Java
Este proyecto es un chat básico basado en sockets TCP en Java que permite la comunicación entre múltiples clientes conectados a un servidor. Incluye funcionalidades para cambiar el nombre de usuario, enviar mensajes privados, mensajes globales y listar usuarios conectados.


Estructura del Proyecto
ServidorChat.java
Programa que actúa como servidor. Escucha conexiones entrantes en el puerto 8080, acepta clientes y los gestiona en threads independientes. Mantiene un mapa sincronizado con los nombres y flujos de salida de los clientes conectados para enviar mensajes.


ClienteChat.java
Programa cliente que se conecta a un servidor específico usando IP y puerto. Envía comandos o mensajes y recibe mensajes desde el servidor en un hilo que escucha continuamente.

Funcionamiento General

Inicio del servidor:
Se ejecuta ServidorChat, que queda esperando conexiones en el puerto definido (8080).


Conexión de clientes:
Los clientes ejecutan ClienteChat y se conectan al servidor ingresando su IP (en local usar 127.0.0.1) y puerto (8080).

Intercambio de mensajes:
Al conectar, el cliente elige un nombre de usuario único.

Comandos disponibles:

/name <nuevo_nombre>: Cambiar el nombre de usuario.

/msg <usuario> <mensaje>: Enviar mensaje privado a otro usuario.

/global <mensaje>: Enviar mensaje global a todos los usuarios conectados.

/clientes: Lista los usuarios conectados.

Manejo de usuarios y mensajes:
El servidor mantiene la lista actualizada y transmite mensajes según comandos, notificando uniones, cambios de nombre y desconexiones.
