package services;

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

public class GeneradorFacturaXml {
    private static FacturaRepository facturaRepository = new FacturaRepositoryImple();

    public static String generarFacturaXML() throws Exception {
        String claveAcceso = null;
        try {
            String fechaEmisionClave = "02062025";
            String tipoComprobante = "01";
            String ruc = "1719284752001";
            String ambiente = "1";
            String establecimiento = "001";
            String puntoEmision = "001";
            String secuencial = facturaRepository.obtenerSiguienteSecuencial(establecimiento, puntoEmision);
            String tipoEmision = "1";
            String codigoNumerico = ClaveAccesoUtil.generarCodigoNumerico();

            claveAcceso = ClaveAccesoUtil.generarClaveAcceso(
                    fechaEmisionClave, tipoComprobante, ruc,
                    ambiente, establecimiento, puntoEmision,
                    secuencial, codigoNumerico, tipoEmision
            );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element factura = doc.createElement("factura");
            factura.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            factura.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
            factura.setAttribute("xsi:noNamespaceSchemaLocation", "factura_V2.0.0.xsd");
            factura.setAttribute("id", "comprobante");
            factura.setAttribute("version", "2.0.0");
            doc.appendChild(factura);

            Element infoTributaria = doc.createElement("infoTributaria");
            factura.appendChild(infoTributaria);
            crearElemento(doc, infoTributaria, "ambiente", ambiente);
            crearElemento(doc, infoTributaria, "tipoEmision", tipoEmision);
            crearElemento(doc, infoTributaria, "razonSocial", "Mi Empresa S.A.");
            crearElemento(doc, infoTributaria, "nombreComercial", "Mi Empresa");
            crearElemento(doc, infoTributaria, "ruc", ruc);
            crearElemento(doc, infoTributaria, "claveAcceso", claveAcceso);
            crearElemento(doc, infoTributaria, "codDoc", tipoComprobante);
            crearElemento(doc, infoTributaria, "estab", establecimiento);
            crearElemento(doc, infoTributaria, "ptoEmi", puntoEmision);
            crearElemento(doc, infoTributaria, "secuencial", secuencial);
            crearElemento(doc, infoTributaria, "dirMatriz", "Av. Principal 123");

            Element infoFactura = doc.createElement("infoFactura");
            factura.appendChild(infoFactura);
            crearElemento(doc, infoFactura, "fechaEmision", "02/06/2025");
            crearElemento(doc, infoFactura, "dirEstablecimiento", "Sucursal 1");
            crearElemento(doc, infoFactura, "tipoIdentificacionComprador", "05");
            crearElemento(doc, infoFactura, "razonSocialComprador", "Juan Pérez");
            crearElemento(doc, infoFactura, "identificacionComprador", "0912345678");
            crearElemento(doc, infoFactura, "totalSinImpuestos", "100.00");
            crearElemento(doc, infoFactura, "totalDescuento", "0.00");

            // totalConImpuestos
            // totalConImpuestos
            Element totalConImpuestos = doc.createElement("totalConImpuestos");
            infoFactura.appendChild(totalConImpuestos);
            Element totalImpuesto = doc.createElement("totalImpuesto");
            totalConImpuestos.appendChild(totalImpuesto);
            crearElemento(doc, totalImpuesto, "codigo", "2");
            crearElemento(doc, totalImpuesto, "codigoPorcentaje", "5"); // 5 = 15% IVA
            // Add descuentoAdicional (even if it's 0.00) if it's expected by the XSD before baseImponible/tarifa
            crearElemento(doc, totalImpuesto, "descuentoAdicional", "0.00"); // Add this line if your XSD expects it.
            crearElemento(doc, totalImpuesto, "baseImponible", "100.00");
            crearElemento(doc, totalImpuesto, "tarifa", "15"); // Move this line after baseImponible
            crearElemento(doc, totalImpuesto, "valor", "15.00");

            crearElemento(doc, infoFactura, "importeTotal", "115.00");
            crearElemento(doc, infoFactura, "moneda", "DOLAR");

            Element pagos = doc.createElement("pagos");
            infoFactura.appendChild(pagos);
            Element pago = doc.createElement("pago");
            pagos.appendChild(pago);
            crearElemento(doc, pago, "formaPago", "01");
            crearElemento(doc, pago, "total", "115.00");

            // detalles
            // detalles
            Element detalles = doc.createElement("detalles");
            factura.appendChild(detalles);
            Element detalle = doc.createElement("detalle");
            detalles.appendChild(detalle);
            crearElemento(doc, detalle, "codigoPrincipal", "PROD001");
            crearElemento(doc, detalle, "descripcion", "Producto de prueba");
            crearElemento(doc, detalle, "cantidad", "2");
            crearElemento(doc, detalle, "precioUnitario", "50.00");
            crearElemento(doc, detalle, "descuento", "0.00");
            crearElemento(doc, detalle, "precioTotalSinImpuesto", "100.00");

            // impuestos dentro de detalle (sin tarifa)
            Element impuestos = doc.createElement("impuestos");
            detalle.appendChild(impuestos);
            Element impuesto = doc.createElement("impuesto");
            impuestos.appendChild(impuesto);
            crearElemento(doc, impuesto, "codigo", "2");
            crearElemento(doc, impuesto, "codigoPorcentaje", "5"); // 15%
            crearElemento(doc, impuesto, "tarifa", "15"); // Add this line and ensure order
            crearElemento(doc, impuesto, "baseImponible", "100.00");
            crearElemento(doc, impuesto, "valor", "15.00");

            Element infoAdicional = doc.createElement("infoAdicional");
            factura.appendChild(infoAdicional);
            Element campoAdicional = doc.createElement("campoAdicional");
            campoAdicional.setAttribute("nombre", "Email");
            campoAdicional.setTextContent("cliente@correo.com");
            infoAdicional.appendChild(campoAdicional);

            // Guardar XML
            String rutaNoFirmado = "src/main/resources/XML/NoFirmados/factura.xml";
            guardarXML(doc, rutaNoFirmado);

            // Firmar XML
            String rutaFirmado = "src/main/resources/XML/Firmados/factura.xml";
            String keystorePath = "src/main/resources/certs/14045426_identity_1719284752.p12";
            String keystorePass = "Elvis2103";
            String alias = "1";
            String keyPass = "Elvis2103";
            FirmarXml.firmarXML(rutaNoFirmado, rutaFirmado, keystorePath, keystorePass, alias, keyPass);

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