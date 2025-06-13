package Wssri;

import jakarta.xml.soap.*;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Wssri {

    public static String autorizarComprobante(String claveAcceso) {

        if (claveAcceso == null || claveAcceso.trim().isEmpty()) {
            return "Clave de acceso no válida.";
        }

        System.setProperty("https.protocols", "TLSv1.2");

        String endpoint = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

        SOAPConnection soapConnection = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("ec", "http://ec.gob.sri.ws.autorizacion");

            SOAPBody soapBody = envelope.getBody();
            SOAPElement autorizacionComprobante = soapBody.addChildElement("autorizacionComprobante", "ec");
            SOAPElement clave = autorizacionComprobante.addChildElement("claveAccesoComprobante");
            clave.addTextNode(claveAcceso);

            soapMessage.saveChanges();

            System.out.println("\nSolicitando autorización al SRI para Clave de Acceso: " + claveAcceso);

            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpoint);

            System.out.println("Respuesta de autorización del SRI recibida.");

            // Create the Aprobacion directory if it doesn't exist
            // MODIFICACIÓN CLAVE AQUÍ: CAMBIAR LA RUTA DE GUARDADO
            File carpetaAprobacion = new File("src/main/resources/Autorizados/");
            if (!carpetaAprobacion.exists()) {
                carpetaAprobacion.mkdirs(); // Creates the directory and any necessary but nonexistent parent directories.
                System.out.println("Carpeta 'src/main/resources/Autorizados/' creada.");
            }

            // Parse the SOAP response
            SOAPBody responseBody = soapResponse.getSOAPBody();
            NodeList autorizaciones = responseBody.getElementsByTagName("autorizacion");

            // Save the complete SOAP response
            File archivoRespuestaCompleta = new File(carpetaAprobacion, claveAcceso + "-respuesta-soap.xml");
            try (OutputStream os = new FileOutputStream(archivoRespuestaCompleta)) {
                soapResponse.writeTo(os);
                System.out.println("Respuesta SOAP completa guardada en: " + archivoRespuestaCompleta.getAbsolutePath());
            }

            if (autorizaciones.getLength() > 0) {
                // Assuming the first "autorizacion" node contains the relevant XML
                String contenidoAutorizacion = autorizaciones.item(0).getTextContent();

                // Guardar solo el nodo <autorizacion> en la carpeta Aprobacion
                File archivoAutorizado = new File(carpetaAprobacion, claveAcceso + "-autorizado.xml");
                try (FileWriter writer = new FileWriter(archivoAutorizado)) {
                    writer.write("<autorizacion>" + contenidoAutorizacion + "</autorizacion>");
                    System.out.println("XML de autorización (recibido del SRI) guardado en: " + archivoAutorizado.getAbsolutePath());
                }

                return contenidoAutorizacion;
            } else {
                System.out.println("No se encontró el nodo <autorizacion> en la respuesta del SRI.");
                return "No se encontró autorización en la respuesta.";
            }

        } catch (Exception ex) {
            Logger.getLogger(Wssri.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error al conectar con el SRI para autorización: " + ex.getMessage());
            return "Error al conectar con el SRI: " + ex.getMessage();
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    Logger.getLogger(Wssri.class.getName()).log(Level.WARNING, "No se pudo cerrar la conexión SOAP", e);
                }
            }
        }
    }
}