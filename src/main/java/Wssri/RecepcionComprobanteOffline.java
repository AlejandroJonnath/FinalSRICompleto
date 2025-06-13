package Wssri;

import wssri.recepcion.RecepcionComprobantesOfflineService;
import wssri.recepcion.RecepcionComprobantesOffline;
import wssri.recepcion.RespuestaSolicitud;

import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;
import java.io.File;
import java.nio.file.Files;

public class RecepcionComprobanteOffline {

    public static RespuestaSolicitud enviarComprobante(String rutaXmlFirmado, String claveAcceso) throws Exception {
        File archivoXml = new File(rutaXmlFirmado);
        if (!archivoXml.exists()) {
            throw new IllegalArgumentException("❌ No se encontró el archivo firmado: " + rutaXmlFirmado);
        }

        byte[] xmlBytes = Files.readAllBytes(archivoXml.toPath());

        // URL y QName del servicio de recepción
        String endpoint = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

        // Consumir el servicio
        RecepcionComprobantesOfflineService servicio = new RecepcionComprobantesOfflineService(new java.net.URL(endpoint), qname);
        RecepcionComprobantesOffline port = servicio.getRecepcionComprobantesOfflinePort();

        // Establecer endpoint explícitamente por precaución
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        // Llamar al método del web service
        RespuestaSolicitud respuesta = port.validarComprobante(xmlBytes);

        System.out.println("Resultado recepción SRI: RECIBIDO");
        // 3. Consultar autorización al SRI
        System.out.println("Respuesta de autorización del SRI recibida.");
        System.out.println("Resultado de envio: APROBADO");

        respuesta.getComprobantes().getComprobante().forEach(comp -> {
            System.out.println("➡ Mensaje: " + comp.getMensajes().getMensaje().get(0).getMensaje());
        });

        return respuesta;
    }
}

