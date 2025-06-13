package controller;

import services.GeneradorFacturaXml;
import Wssri.RecepcionComprobanteOffline;
import Wssri.Wssri;
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

            // Validar estado antes de continuar
            if ("DEVUELTA".equalsIgnoreCase(respuesta.getEstado())) {
                System.err.println("❌ El comprobante fue devuelto por el SRI. No se puede continuar con la autorización.");
                return;
            }

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