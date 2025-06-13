package services;

import model.DetalleFactura;
import model.Factura;
import model.Producto;
import org.w3c.dom.*;
import repository.FacturaRepository;
import repository.FacturaRepositoryImple;
import util.ClaveAccesoUtil;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class GeneradorFacturaXml {

    private static final String RUTA_NO_FIRMADOS = "C:/Users/David Ruiz/IdeaProjects/SRI-Facturacion/src/main/resources/XML/NoFirmados/";
    private static final String RUTA_FIRMADOS    = "C:/Users/David Ruiz/IdeaProjects/SRI-Facturacion/src/main/resources/XML/Firmados/";

    private static FacturaRepository facturaRepository = new FacturaRepositoryImple();

    public static String generarFacturaXML(int facturaId) throws Exception {
        String claveAcceso = null;
        try {
            // Obtener factura y detalles reales
            Factura factura = facturaRepository.obtenerFacturaPorId(facturaId);
            List<DetalleFactura> detalles = facturaRepository.obtenerDetallesPorFactura(facturaId);

            claveAcceso = factura.getClaveAcceso();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element facturaElement = doc.createElement("factura");
            facturaElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            facturaElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
            facturaElement.setAttribute("xsi:noNamespaceSchemaLocation", "factura_V2.0.0.xsd");
            facturaElement.setAttribute("id", "comprobante");
            facturaElement.setAttribute("version", "2.0.0");
            doc.appendChild(facturaElement);

            // infoTributaria
            Element infoTributaria = doc.createElement("infoTributaria");
            facturaElement.appendChild(infoTributaria);
            crearElemento(doc, infoTributaria, "ambiente", "1");
            crearElemento(doc, infoTributaria, "tipoEmision", factura.getTipoEmision());
            crearElemento(doc, infoTributaria, "razonSocial", "Mi Empresa S.A.");
            crearElemento(doc, infoTributaria, "nombreComercial", "Mi Empresa");
            crearElemento(doc, infoTributaria, "ruc", "1719284752001");
            crearElemento(doc, infoTributaria, "claveAcceso", claveAcceso);
            crearElemento(doc, infoTributaria, "codDoc", "01");
            crearElemento(doc, infoTributaria, "estab", factura.getEstablecimiento());
            crearElemento(doc, infoTributaria, "ptoEmi", factura.getPuntoEmision());
            crearElemento(doc, infoTributaria, "secuencial", factura.getSecuencial());
            crearElemento(doc, infoTributaria, "dirMatriz", "Av. Principal 123");

            // infoFactura
            Element infoFactura = doc.createElement("infoFactura");
            facturaElement.appendChild(infoFactura);
            crearElemento(doc, infoFactura, "fechaEmision", "02/06/2025"); // puedes mejorar con SimpleDateFormat
            crearElemento(doc, infoFactura, "dirEstablecimiento", factura.getClienteDireccion());
            crearElemento(doc, infoFactura, "tipoIdentificacionComprador", "05");
            crearElemento(doc, infoFactura, "razonSocialComprador", factura.getClienteNombre());
            crearElemento(doc, infoFactura, "identificacionComprador", factura.getClienteCedula());
            crearElemento(doc, infoFactura, "totalSinImpuestos", factura.getTotal().toPlainString());
            crearElemento(doc, infoFactura, "totalDescuento", "0.00");

            // totalConImpuestos
            Element totalConImpuestos = doc.createElement("totalConImpuestos");
            infoFactura.appendChild(totalConImpuestos);
            Element totalImpuesto = doc.createElement("totalImpuesto");
            totalConImpuestos.appendChild(totalImpuesto);

            BigDecimal totalIva = detalles.stream()
                    .map(DetalleFactura::getIvaValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            crearElemento(doc, totalImpuesto, "codigo", "2");
            crearElemento(doc, totalImpuesto, "codigoPorcentaje", "5");
            crearElemento(doc, totalImpuesto, "descuentoAdicional", "0.00");
            crearElemento(doc, totalImpuesto, "baseImponible", factura.getTotal().toPlainString());
            crearElemento(doc, totalImpuesto, "tarifa", "15");
            crearElemento(doc, totalImpuesto, "valor", totalIva.toPlainString());

            BigDecimal importeTotal = factura.getTotal().add(totalIva);

            crearElemento(doc, infoFactura, "importeTotal", importeTotal.toPlainString());
            crearElemento(doc, infoFactura, "moneda", "DOLAR");

            // pagos
            Element pagos = doc.createElement("pagos");
            infoFactura.appendChild(pagos);
            Element pago = doc.createElement("pago");
            pagos.appendChild(pago);
            crearElemento(doc, pago, "formaPago", "01");
            crearElemento(doc, pago, "total", importeTotal.toPlainString());

            // detalles
            Element detallesElement = doc.createElement("detalles");
            facturaElement.appendChild(detallesElement);

            for (DetalleFactura detalle : detalles) {
                Element detalleElement = doc.createElement("detalle");
                detallesElement.appendChild(detalleElement);

                crearElemento(doc, detalleElement, "codigoPrincipal", String.valueOf(detalle.getProducto().getId()));
                crearElemento(doc, detalleElement, "descripcion", detalle.getProducto().getNombre());
                crearElemento(doc, detalleElement, "cantidad", String.valueOf(detalle.getCantidad()));
                crearElemento(doc, detalleElement, "precioUnitario", detalle.getPrecioUnitario().toPlainString());
                crearElemento(doc, detalleElement, "descuento", detalle.getDescuento().toPlainString());
                crearElemento(doc, detalleElement, "precioTotalSinImpuesto", detalle.getSubtotal().toPlainString());

                // impuestos dentro de detalle
                Element impuestos = doc.createElement("impuestos");
                detalleElement.appendChild(impuestos);
                Element impuesto = doc.createElement("impuesto");
                impuestos.appendChild(impuesto);
                crearElemento(doc, impuesto, "codigo", "2");
                crearElemento(doc, impuesto, "codigoPorcentaje", "5");
                crearElemento(doc, impuesto, "tarifa", detalle.getIva().toPlainString());
                crearElemento(doc, impuesto, "baseImponible", detalle.getSubtotal().toPlainString());
                crearElemento(doc, impuesto, "valor", detalle.getIvaValor().toPlainString());
            }

            // infoAdicional
            Element infoAdicional = doc.createElement("infoAdicional");
            facturaElement.appendChild(infoAdicional);
            Element campoAdicional = doc.createElement("campoAdicional");
            campoAdicional.setAttribute("nombre", "Email");
            campoAdicional.setTextContent(factura.getClienteEmail());
            infoAdicional.appendChild(campoAdicional);

            // Guardar XML
            String rutaNoFirmado = RUTA_NO_FIRMADOS + "factura_" + facturaId + ".xml";
            guardarXML(doc, rutaNoFirmado);

            // Log bonito
            System.out.println("===============================================");
            System.out.println("[INFO] ✅ XML generado correctamente para facturaId = " + facturaId);
            System.out.println("[INFO] ✅ Clave de Acceso = " + claveAcceso);
            System.out.println("[INFO] ✅ Archivo generado en (NO FIRMADO): " + rutaNoFirmado);
            System.out.println("===============================================");

            // Ruta del firmado
            String rutaFirmado = RUTA_FIRMADOS + "factura_" + facturaId + ".xml";

// Parámetros para firmar
            String keystorePath = "C:/Users/David Ruiz/IdeaProjects/SRI-Facturacion/src/main/resources/certs/14045426_identity_1719284752.p12";
            String keystorePass = "Elvis2103";
            String alias = "1";
            String keyPass = "Elvis2103";

// Firmar el XML
            FirmarXml.firmarXML(rutaNoFirmado, rutaFirmado, keystorePath, keystorePass, alias, keyPass);

// Log bonito del firmado
            System.out.println("===============================================");
            System.out.println("[INFO] ✅ XML FIRMADO correctamente para facturaId = " + facturaId);
            System.out.println("[INFO] ✅ Archivo firmado generado en: " + rutaFirmado);
            System.out.println("===============================================");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al generar la factura electrónica", e);
        }
        return claveAcceso;
    }

    private static void crearElemento(Document doc, Element padre, String nombre, String valor) {
        Element elem = doc.createElement(nombre);
        elem.setTextContent(valor);
        padre.appendChild(elem);
    }

    private static void guardarXML(Document doc, String ruta) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        File archivo = new File(ruta);
        archivo.getParentFile().mkdirs();
        transformer.transform(new DOMSource(doc), new StreamResult(archivo));
    }
}