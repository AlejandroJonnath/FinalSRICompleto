package Wssri;

import wssri.recepcion.RespuestaSolicitud;
import wssri.recepcion.RecepcionComprobantesOfflineService;

import java.io.File;
import java.nio.file.Files;

public class RecepcionComprobanteOffline {

    private static final String URL_PRUEBAS = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";

    public static String enviarComprobante(String rutaArchivoFirmado) {
        System.out.println("Método enviarComprobante iniciado.");
        try {
            File xmlFile = new File(rutaArchivoFirmado);
            if (!xmlFile.exists()) {
                System.err.println("El archivo no existe: " + rutaArchivoFirmado);
                return "ERROR";
            }

            byte[] archivoBytes = Files.readAllBytes(xmlFile.toPath());
            System.out.println("Archivo leído con éxito, tamaño: " + archivoBytes.length + " bytes.");

            RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService();
            wssri.recepcion.RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();

            RespuestaSolicitud respuesta = port.validarComprobante(archivoBytes);
            System.out.println("Respuesta del SRI recibida.");

            if (respuesta == null) {
                System.out.println("Respuesta nula del servicio de recepción.");
                return "ERROR";
            }

            String estado = respuesta.getEstado();
            System.out.println("Estado de la respuesta: " + estado);

            if (respuesta.getComprobantes() != null && respuesta.getComprobantes().getComprobante() != null) {
                respuesta.getComprobantes().getComprobante().forEach(comp -> {
                    System.out.println("Clave de Acceso: " + comp.getClaveAcceso());
                    if (comp.getMensajes() != null && comp.getMensajes().getMensaje() != null) {
                        comp.getMensajes().getMensaje().forEach(mensaje -> {
                            System.out.println("Mensaje: " + mensaje.getMensaje());
                            System.out.println("Información adicional: " + mensaje.getInformacionAdicional());
                        });
                    }
                });
            }

            return estado != null ? estado : "ERROR";

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al enviar comprobante al SRI.");
            return "ERROR";
        }
    }
}
