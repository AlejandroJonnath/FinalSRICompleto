package controller;

import services.GeneradorFacturaXml;
import Wssri.RecepcionComprobanteOffline;
import repository.FacturaRepository;
import repository.FacturaRepositoryImple;
import wssri.recepcion.RespuestaSolicitud;

public class FacturaController {

    public static void procesarFactura() {
        try {
            // Obtener el último facturaId
            FacturaRepository facturaRepo = new FacturaRepositoryImple();
            int facturaId = facturaRepo.obtenerUltimoFacturaId();

            if (facturaId == -1) {
                System.err.println("❌ No hay facturas en la base de datos.");
                return;
            }

            // 1. Generar XML y firmarlo
            String claveAcceso = GeneradorFacturaXml.generarFacturaXML(facturaId);
            System.out.println("Clave de acceso generada: " + claveAcceso);

            // 2. Enviar XML firmado a Recepción
            String rutaFirmado = "src/main/resources/XML/Firmados/factura_" + facturaId + ".xml";
            System.out.println("Enviando comprobante firmado desde: " + rutaFirmado);

            // Recibir respuesta del SRI
            RespuestaSolicitud respuesta = RecepcionComprobanteOffline.enviarComprobante(rutaFirmado, claveAcceso);



        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al procesar la factura.");
        }
    }

    public static void main(String[] args) {
        procesarFactura();
    }
}