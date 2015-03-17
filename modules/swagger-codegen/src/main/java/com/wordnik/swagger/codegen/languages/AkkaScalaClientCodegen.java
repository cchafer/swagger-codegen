package com.wordnik.swagger.codegen.languages;

import com.google.common.base.CaseFormat;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.wordnik.swagger.codegen.*;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import com.wordnik.swagger.models.properties.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class AkkaScalaClientCodegen extends DefaultCodegen implements CodegenConfig {
  Logger LOGGER = LoggerFactory.getLogger(AkkaScalaClientCodegen.class);

  protected String mainPackage = "io.swagger.client";

  protected String invokerPackage = mainPackage + ".core";
  protected String groupId = "com.wordnik";
  protected String artifactId = "swagger-client";
  protected String artifactVersion = "1.0.0";
  protected String sourceFolder = "src/main/scala";
  protected String resourcesFolder = "src/main/resources";
  protected String configKey = "apiRequest";
  protected int defaultTimeoutInMs = 5000;
  protected String configKeyPath = mainPackage;
  protected String authScheme = "";
  protected boolean authPreemptive = false;
  protected boolean asyncHttpClient = !authScheme.isEmpty();
  protected boolean registerNonStandardStatusCodes = true;
  protected boolean renderJavadoc = true;
  protected boolean removeOAuthSecurities = true;


  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  public String getName() {
    return "akka-scala";
  }

  public String getHelp() {
    return "Generates a Scala client library.";
  }

  public AkkaScalaClientCodegen() {
    super();
    outputFolder = "generated-code/scala";
    modelTemplateFiles.put("model.mustache", ".scala");
    apiTemplateFiles.put("api.mustache", ".scala");
    templateDir = "akka-scala";
    apiPackage = mainPackage + ".api";
    modelPackage = mainPackage + ".model";

    reservedWords = new HashSet<String>(
        Arrays.asList(
            "abstract", "case", "catch", "class", "def", "do", "else", "extends",
            "false", "final", "finally", "for", "forSome", "if", "implicit",
            "import", "lazy", "match", "new", "null", "object", "override", "package",
            "private", "protected", "return", "sealed", "super", "this", "throw",
            "trait", "try", "true", "type", "val", "var", "while", "with", "yield")
    );

    additionalProperties.put("invokerPackage", invokerPackage);
    additionalProperties.put("groupId", groupId);
    additionalProperties.put("artifactId", artifactId);
    additionalProperties.put("artifactVersion", artifactVersion);
    additionalProperties.put("asyncHttpClient", asyncHttpClient);
    additionalProperties.put("authScheme", authScheme);
    additionalProperties.put("authPreemptive", authPreemptive);
    additionalProperties.put("configKey", configKey);
    additionalProperties.put("configKeyPath", configKeyPath);
    additionalProperties.put("defaultTimeout", defaultTimeoutInMs);
    if (renderJavadoc) {
      additionalProperties.put("javadocRenderer", new JavadocLambda());
    }

    supportingFiles.add(new SupportingFile("pom.mustache", "", "pom.xml"));
    supportingFiles.add(new SupportingFile("reference.mustache", resourcesFolder, "reference.conf"));
    supportingFiles.add(new SupportingFile("apiRequest.mustache",
        (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "ApiRequest.scala"));
    supportingFiles.add(new SupportingFile("apiInvoker.mustache",
        (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "ApiInvoker.scala"));
    supportingFiles.add(new SupportingFile("apiSettings.mustache",
        (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "ApiSettings.scala"));

    importMapping.remove("Seq");
    importMapping.remove("List");
    importMapping.remove("Set");
    importMapping.remove("Map");

    importMapping.put("DateTime", "org.joda.time.DateTime");
    importMapping.put("ListBuffer", "scala.collections.mutable.ListBuffer");

    typeMapping = new HashMap<String, String>();
    typeMapping.put("enum", "NSString");
    typeMapping.put("array", "Seq");
    typeMapping.put("set", "Set");
    typeMapping.put("boolean", "Boolean");
    typeMapping.put("string", "String");
    typeMapping.put("int", "Int");
    typeMapping.put("long", "Long");
    typeMapping.put("float", "Float");
    typeMapping.put("byte", "Byte");
    typeMapping.put("short", "Short");
    typeMapping.put("char", "Char");
    typeMapping.put("long", "Long");
    typeMapping.put("double", "Double");
    typeMapping.put("object", "Any");
    typeMapping.put("file", "File");

    languageSpecificPrimitives = new HashSet<String>(
        Arrays.asList(
            "String",
            "boolean",
            "Boolean",
            "Double",
            "Int",
            "Long",
            "Float",
            "Object",
            "List",
            "Seq",
            "Map")
    );
    instantiationTypes.put("array", "ListBuffer");
    instantiationTypes.put("map", "Map");
  }

  @Override
  public String escapeReservedWord(String name) {
    return "`" + name + "`";
  }

  @Override
  public String apiFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + apiPackage().replace('.', File.separatorChar);
  }

  public String modelFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + modelPackage().replace('.', File.separatorChar);
  }

  @Override
  public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
    if (registerNonStandardStatusCodes) {
      try {
        @SuppressWarnings("unchecked")
        Map<String, ArrayList<CodegenOperation>> opsMap = (Map<String, ArrayList<CodegenOperation>>) objs.get("operations");
        HashSet<Integer> unknownCodes = new HashSet<Integer>();
        for (CodegenOperation operation : opsMap.get("operation")) {
          for (CodegenResponse response : operation.responses) {
            if ("default".equals(response.code))
              continue;
            try {
              int code = Integer.parseInt(response.code);
              if (code >= 600) {
                unknownCodes.add(code);
              }
            } catch (NumberFormatException e) {
              LOGGER.error("Status code is not an integer : response.code", e);
            }
          }
        }
        if (!unknownCodes.isEmpty()) {
          additionalProperties.put("unknownStatusCodes", unknownCodes);
        }
      } catch (Exception e) {
        LOGGER.error("Unable to find operations List", e);
      }
    }
    return super.postProcessOperations(objs);
  }

  @Override
  public String getTypeDeclaration(Property p) {
    if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getSwaggerType(p) + "[" + getTypeDeclaration(inner) + "]";
    } else if (p instanceof MapProperty) {
      MapProperty mp = (MapProperty) p;
      Property inner = mp.getAdditionalProperties();

      return getSwaggerType(p) + "[String, " + getTypeDeclaration(inner) + "]";
    }
    return super.getTypeDeclaration(p);
  }

  @Override
  public List<CodegenSecurity> fromSecurity(Map<String, SecuritySchemeDefinition> schemes) {
    final List<CodegenSecurity> codegenSecurities = super.fromSecurity(schemes);
    if (!removeOAuthSecurities)
      return codegenSecurities;

    // Remove OAuth securities
    Iterator<CodegenSecurity> it = codegenSecurities.iterator();
    while (it.hasNext()) {
      final CodegenSecurity security = it.next();
      if (security.isOAuth)
        it.remove();
    }
    // Adapt 'hasMore'
    it = codegenSecurities.iterator();
    while (it.hasNext()) {
      final CodegenSecurity security = it.next();
      security.hasMore = it.hasNext();
    }

    if (codegenSecurities.isEmpty())
      return null;
    return codegenSecurities;
  }

  @Override
  public String toOperationId(String operationId) {
    return super.toOperationId(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, operationId));
  }

  @Override
  public String toParamName(String name) {
    return super.toParamName(camelize(name));
  }

  @Override
  public String toVarName(String name) {
    return super.toVarName(camelize(name));
  }

  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if (typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if (languageSpecificPrimitives.contains(type))
        return toModelName(type);
    } else
      type = swaggerType;
    return toModelName(type);
  }

  @Override
  public String toInstantiationType(Property p) {
    if (p instanceof MapProperty) {
      MapProperty ap = (MapProperty) p;
      String inner = getSwaggerType(ap.getAdditionalProperties());
      return instantiationTypes.get("map") + "[String, " + inner + "]";
    } else if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      String inner = getSwaggerType(ap.getItems());
      return instantiationTypes.get("array") + "[" + inner + "]";
    } else
      return null;
  }

  public String toDefaultValue(Property p) {
    if (!p.getRequired())
      return "None";
    if (p instanceof StringProperty)
      return "null";
    else if (p instanceof BooleanProperty)
      return "null";
    else if (p instanceof DateProperty)
      return "null";
    else if (p instanceof DateTimeProperty)
      return "null";

    else if (p instanceof DoubleProperty)
      return "null";
    else if (p instanceof FloatProperty)
      return "null";
    else if (p instanceof IntegerProperty)
      return "null";
    else if (p instanceof LongProperty)
      return "null";
    else if (p instanceof MapProperty) {
      MapProperty ap = (MapProperty) p;
      String inner = getSwaggerType(ap.getAdditionalProperties());
      return "Map[String, " + inner + "].empty ";
    } else if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      String inner = getSwaggerType(ap.getItems());
      return "Seq[" + inner + "].empty ";
    } else
      return "null";
  }

  private String camelize(String value) {
    String[] strings = StringUtils.split(value, "_");
    for (int i = 1; i < strings.length; i++) {
      strings[i] = StringUtils.capitalize(strings[i]);
    }
    return StringUtils.join(strings);
  }

  private static class JavadocLambda implements Mustache.Lambda {
    @Override
    public void execute(Template.Fragment frag, Writer out) throws IOException {
      final StringWriter tempWriter = new StringWriter();
      frag.execute(tempWriter);
      final String renderedFrag = tempWriter.toString();
      final String[] lines = renderedFrag.split("\\r?\\n");
      final StringBuilder sb = new StringBuilder();
      sb.append("  /**\n");
      for (String line : lines) {
        sb.append("   * ").append(line).append("\n");
      }
      sb.append("   */\n");
      out.write(sb.toString());
    }
  }
}