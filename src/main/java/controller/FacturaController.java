
package controller;

import services.GeneradorFacturaXml;
import Wssri.RecepcionComprobanteOffline;
import Wssri.Wssri;

public class FacturaController {

    public static void procesarFactura() {
        try {
            // 1. Generar XML y firmarlo
            String claveAcceso = GeneradorFacturaXml.generarFacturaXML();
            System.out.println("Clave de acceso generada: " + claveAcceso);

            // 2. Enviar XML firmado a Recepción
            String rutaFirmado = "src/main/resources/XML/Firmados/factura.xml";
            System.out.println("Enviando comprobante firmado desde: " + rutaFirmado);
            RecepcionComprobanteOffline.enviarComprobante(rutaFirmado);

            // 3. Consultar autorización al SRI
            String resultadoAutorizacion = Wssri.autorizarComprobante(claveAcceso);
            System.out.println("Resultado autorización SRI: " + resultadoAutorizacion);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al procesar la factura.");
        }
    }

    public static void main(String[] args) {
        procesarFactura();
    }
}

