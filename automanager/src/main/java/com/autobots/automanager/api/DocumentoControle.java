package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Documento;
import org.springframework.web.bind.annotation.*;

@RestController("apiDocumentoControle")
@RequestMapping("/api/documentos")
public class DocumentoControle extends CadastroControle<Documento> {
  public DocumentoControle(DocumentoService service) {
    super(service, "documentos");
  }
}
