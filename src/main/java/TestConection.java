import java.io.File;
// Importa clases para validación XML contra un esquema XSD
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 * Clase utilitaria para validar un archivo XML contra su correspondiente archivo XSD.
 */
public class TestConection {

    /**
     * Valida si un XML cumple con la estructura definida en un archivo XSD.
     *
     * @param xmlPath Ruta del archivo XML.
     * @param xsdPath Ruta del archivo XSD.
     * @return true si el XML es válido; false si hay errores.
     */
    public static boolean validarXML(String xmlPath, String xsdPath) {
        try {
            // Crea instancias de los archivos XSD y XML
            File schemaFile = new File(xsdPath);
            File xmlFile = new File(xmlPath);

            // Verifica que el archivo XSD exista
            if (!schemaFile.exists()) {
                System.err.println("❌ El archivo XSD no existe: " + xsdPath);
                return false;
            }

            // Verifica que el archivo XML exista
            if (!xmlFile.exists()) {
                System.err.println("❌ El archivo XML no existe: " + xmlPath);
                return false;
            }

            // Crea un validador basado en el estándar W3C XML Schema
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(schemaFile);
            Validator validator = schema.newValidator();

            // Ejecuta la validación del XML contra el esquema
            validator.validate(new StreamSource(xmlFile));
            System.out.println("✅ XML válido contra el XSD.");
            return true;

        } catch (SAXParseException e) {
            // Captura errores específicos de validación con ubicación
            System.err.println("❌ Error de validación:");
            System.err.println("Línea: " + e.getLineNumber());
            System.err.println("Columna: " + e.getColumnNumber());
            System.err.println("Mensaje : " + e.getMessage());
        } catch (SAXException e) {
            // Captura errores de validación generales
            System.err.println("❌ Error de validación: " + e.getMessage());
        } catch (Exception e) {
            // Captura otros errores inesperados (por ejemplo, IO)
            System.err.println("❌ Error general: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        // Rutas relativas al proyecto, para validar una factura firmada
        String xmlFile = "src/main/resources/XML/Firmados/factura.xml";
        String xsdFile = "src/main/resources/XSD/factura_V2.0.0.xsd";

        // Ejecuta la validación del XML contra el XSD
        validarXML(xmlFile, xsdFile);
    }
}

