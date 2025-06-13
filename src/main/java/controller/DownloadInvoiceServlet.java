package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.Conexion;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/downloadInvoice")
public class DownloadInvoiceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String invoiceIdParam = request.getParameter("invoiceId");
        if (invoiceIdParam == null) {
            response.sendRedirect("productos");
            return;
        }

        int invoiceId;
        try {
            invoiceId = Integer.parseInt(invoiceIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("productos");
            return;
        }

        String sqlFactura = "SELECT id, fecha, cliente_nombre, cliente_cedula, cliente_direccion, cliente_email, cliente_telefono, " +
                "establecimiento, punto_emision, secuencial, clave_acceso, tipo_emision, total " +
                "FROM factura WHERE id = ?";

        String sqlDetalles = "SELECT p.nombre, p.marca, df.precio_unitario, df.cantidad, df.subtotal, df.descuento, df.iva " +
                "FROM detalle_factura df " +
                "JOIN productos p ON df.producto_id = p.id " +
                "WHERE df.factura_id = ?";

        Date fechaFactura = null;
        String clienteNombre = "", clienteCedula = "", clienteDireccion = "", clienteEmail = "", clienteTelefono = "";
        String establecimiento = "", puntoEmision = "", secuencial = "", claveAcceso = "", tipoEmision = "";
        double totalFactura = 0.0;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFactura)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fechaFactura = rs.getTimestamp("fecha");
                    clienteNombre = rs.getString("cliente_nombre");
                    clienteCedula = rs.getString("cliente_cedula");
                    clienteDireccion = rs.getString("cliente_direccion");
                    clienteEmail = rs.getString("cliente_email");
                    clienteTelefono = rs.getString("cliente_telefono");
                    establecimiento = rs.getString("establecimiento");
                    puntoEmision = rs.getString("punto_emision");
                    secuencial = rs.getString("secuencial");
                    claveAcceso = rs.getString("clave_acceso");
                    tipoEmision = rs.getString("tipo_emision");
                    totalFactura = rs.getDouble("total");
                } else {
                    response.sendRedirect("productos");
                    return;
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error al leer datos de factura: " + e.getMessage(), e);
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"factura_" + invoiceId + ".pdf\"");

        try {
            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontCelda1Normal = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font fontCelda1Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // === ENCABEZADO ===
            PdfPTable encabezado = new PdfPTable(3);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{4f, 0.3f, 3f});

            PdfPTable colIzquierda = new PdfPTable(1);
            colIzquierda.setWidthPercentage(100);

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            try {
                String logoPath = getServletContext().getRealPath("src/main/resources/img/logo1.jpg");
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(90, 45);
                logoCell.addElement(logo);
            } catch (Exception ex) {
                logoCell.addElement(new Paragraph("MIMIR PETSHOP", fontNormal));
            }
            logoCell.setPaddingBottom(2f);
            colIzquierda.addCell(logoCell);

            PdfPCell cel1 = new PdfPCell();
            cel1.setBorder(Rectangle.BOX);
            cel1.setPadding(3);
            cel1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cel1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cel1.addElement(new Paragraph("MIMIR PETSHOP", fontCelda1Bold));
            cel1.addElement(new Paragraph("PRODUCTOS PARA MASCOTAS", fontCelda1Bold));
            cel1.addElement(new Paragraph("Dirección: Miguel Anguel Zambrano 703 y E15a", fontCelda1Normal));
            cel1.addElement(new Paragraph("Contabilidad: NO", fontCelda1Normal));
            cel1.addElement(new Paragraph("Teléfono: 0987032982" ,fontCelda1Normal));
            colIzquierda.addCell(cel1);

            PdfPCell col1 = new PdfPCell(colIzquierda);
            col1.setBorder(Rectangle.NO_BORDER);
            encabezado.addCell(col1);

            PdfPCell espacio = new PdfPCell();
            espacio.setBorder(Rectangle.NO_BORDER);
            encabezado.addCell(espacio);

            PdfPCell cel3 = new PdfPCell();
            cel3.setBorder(Rectangle.BOX);
            cel3.setPadding(5);
            cel3.setVerticalAlignment(Element.ALIGN_TOP);

            cel3.addElement(new Paragraph("Establecimiento: " + establecimiento, fontNormal));
            cel3.addElement(new Paragraph("Punto Emisión: " + puntoEmision, fontNormal));
            cel3.addElement(new Paragraph("Secuencial: " + secuencial, fontNormal));
            cel3.addElement(new Paragraph("Tipo Emisión: " + tipoEmision, fontNormal));
            cel3.addElement(new Paragraph("CLAVE DE ACCESO:", fontNormal));
            cel3.addElement(new Paragraph(claveAcceso, fontNormal));

            PdfContentByte cb = writer.getDirectContent();
            Barcode128 barcode = new Barcode128();
            barcode.setCode(claveAcceso);
            barcode.setCodeType(Barcode.CODE128);
            Image barcodeImage = barcode.createImageWithBarcode(cb, null, null);
            barcodeImage.scaleToFit(200, 40);
            barcodeImage.setAlignment(Element.ALIGN_CENTER);
            cel3.addElement(Chunk.NEWLINE);
            cel3.addElement(barcodeImage);

            encabezado.addCell(cel3);
            document.add(encabezado);
            document.add(Chunk.NEWLINE);

            // === DATOS DEL CLIENTE ===
            document.add(Chunk.NEWLINE);

            PdfPTable clienteTable = new PdfPTable(3);
            clienteTable.setWidthPercentage(100);
            clienteTable.setWidths(new float[]{3f, 2f, 3f});
            clienteTable.addCell(getCell("Razón Social / Nombres y Apellidos: " + clienteNombre, PdfPCell.ALIGN_LEFT, fontNormal));
            clienteTable.addCell(getCell("RUC / CI: " + clienteCedula, PdfPCell.ALIGN_LEFT, fontNormal));
            clienteTable.addCell(getCell("Fecha Emisión: " + sdf.format(fechaFactura), PdfPCell.ALIGN_LEFT, fontNormal));

            document.add(clienteTable);
            document.add(Chunk.NEWLINE);

            // === DETALLES ===
            PdfPTable detalle = new PdfPTable(7);
            detalle.setWidthPercentage(100);
            detalle.setWidths(new float[]{1.5f, 1.5f, 1.2f, 3f, 1.5f, 1.5f, 1.5f});
            detalle.addCell(getCell("Cod. Principal", PdfPCell.ALIGN_CENTER, fontSubtitulo));
            detalle.addCell(getCell("Cod. Auxiliar", PdfPCell.ALIGN_CENTER, fontSubtitulo));
            detalle.addCell(getCell("Cant.", PdfPCell.ALIGN_CENTER, fontSubtitulo));
            detalle.addCell(getCell("Descripción", PdfPCell.ALIGN_CENTER, fontSubtitulo));
            detalle.addCell(getCell("Precio Unitario", PdfPCell.ALIGN_CENTER, fontSubtitulo));
            detalle.addCell(getCell("Descuento", PdfPCell.ALIGN_CENTER, fontSubtitulo));
            detalle.addCell(getCell("Precio Total", PdfPCell.ALIGN_CENTER, fontSubtitulo));

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement psDet = conn.prepareStatement(sqlDetalles)) {
                psDet.setInt(1, invoiceId);
                try (ResultSet rsDet = psDet.executeQuery()) {
                    while (rsDet.next()) {
                        detalle.addCell(getCell("18", PdfPCell.ALIGN_LEFT, fontNormal));
                        detalle.addCell(getCell("2", PdfPCell.ALIGN_LEFT, fontNormal));
                        detalle.addCell(getCell(String.valueOf(rsDet.getInt("cantidad")), PdfPCell.ALIGN_CENTER, fontNormal));
                        detalle.addCell(getCell(rsDet.getString("nombre") + " " + rsDet.getString("marca"), PdfPCell.ALIGN_LEFT, fontNormal));
                        detalle.addCell(getCell(String.format("%.2f", rsDet.getDouble("precio_unitario")), PdfPCell.ALIGN_RIGHT, fontNormal));
                        detalle.addCell(getCell(String.format("%.2f", rsDet.getDouble("descuento")), PdfPCell.ALIGN_RIGHT, fontNormal));
                        detalle.addCell(getCell(String.format("%.2f", rsDet.getDouble("subtotal")), PdfPCell.ALIGN_RIGHT, fontNormal));
                    }
                }
            }

            document.add(detalle);
            document.add(Chunk.NEWLINE);

            // === TOTALES ===
            PdfPTable totales = new PdfPTable(2);
            totales.setWidthPercentage(40);
            totales.setHorizontalAlignment(Element.ALIGN_RIGHT);

// Cambiado de 1.12 a 1.15 para reflejar el 15% de IVA
            double subtotalSinIva = totalFactura / 1.15;
            double iva15 = totalFactura - subtotalSinIva;

// Cambiar las etiquetas para reflejar el 15% en lugar del 12%
            totales.addCell(getCell("SUBTOTAL 15%", PdfPCell.ALIGN_LEFT, fontNormal));
            totales.addCell(getCell(String.format("%.2f", subtotalSinIva), PdfPCell.ALIGN_RIGHT, fontNormal));
            totales.addCell(getCell("SUBTOTAL 0%", PdfPCell.ALIGN_LEFT, fontNormal));
            totales.addCell(getCell("0.00", PdfPCell.ALIGN_RIGHT, fontNormal));
            totales.addCell(getCell("SUBTOTAL SIN IMPUESTOS", PdfPCell.ALIGN_LEFT, fontNormal));
            totales.addCell(getCell(String.format("%.2f", subtotalSinIva), PdfPCell.ALIGN_RIGHT, fontNormal));
            totales.addCell(getCell("DESCUENTO", PdfPCell.ALIGN_LEFT, fontNormal));
            totales.addCell(getCell("0.00", PdfPCell.ALIGN_RIGHT, fontNormal));
            totales.addCell(getCell("IVA 15%", PdfPCell.ALIGN_LEFT, fontNormal));  // Cambiado de 12% a 15%
            totales.addCell(getCell(String.format("%.2f", iva15), PdfPCell.ALIGN_RIGHT, fontNormal));  // Usando iva15 en lugar de iva12
            totales.addCell(getCell("VALOR TOTAL", PdfPCell.ALIGN_LEFT, fontSubtitulo));
            totales.addCell(getCell(String.format("%.2f", totalFactura), PdfPCell.ALIGN_RIGHT, fontSubtitulo));
            document.add(totales);
            document.add(Chunk.NEWLINE);

            // === INFORMACIÓN ADICIONAL ===
            Paragraph infoAdicional = new Paragraph("Información Adicional:\n" +
                    "Email: " + clienteEmail + "\n" +
                    "Dirección: " + clienteDireccion + "\n"+
                    "Teléfono: " + clienteTelefono + "\n"+
                    "Gracias por su compra!", fontNormal);
            document.add(infoAdicional);

            document.close();

        } catch (DocumentException | SQLException e) {
            throw new ServletException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    private PdfPCell getCell(String texto, int align, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        return cell;
    }
}