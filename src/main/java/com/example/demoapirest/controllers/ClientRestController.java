package com.example.demoapirest.controllers;

import com.example.demoapirest.models.entity.Cliente;
import com.example.demoapirest.models.services.IClienteServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*"}) // Dominios permiticos por cors
@RestController
@RequestMapping("/api")
public class ClientRestController {

    @Autowired
    private IClienteServie clienteServie;

    @GetMapping("/clientes")
    public List<Cliente> index() {
        return this.clienteServie.findAll();
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> show (@PathVariable Long id) {

        Cliente cliente = null;
        Map<String, Object> response = new HashMap<>();
        try {

            cliente = this.clienteServie.findById(id);

        } catch (DataAccessException e) {

            response.put("mensaje", "Error al consultar en la base de datos, por favor contacte con el administrador");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        if (cliente == null) {
            response.put("mensaje", "El cliente con id: " + id + "".concat(" no existe en la base de datos!"));
            return new ResponseEntity<Map <String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create (@Valid @RequestBody Cliente cliente, BindingResult result) {

        Cliente newClient = null;
        Map<String, Object> response = new HashMap<String, Object>();

        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();

            /*result.getFieldErrors().forEach(err -> {
                errors.add("campo: '" + err.getField() + "' " + err.getDefaultMessage());
            });*/

            /*for (FieldError err: result.getFieldErrors()) {
                errors.add("campo: '" + err.getField() + "' " + err.getDefaultMessage());
            }*/

            errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "campo: '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());


            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {

            newClient = this.clienteServie.save(cliente);

        } catch (DataAccessException e) {

            response.put("message", "Por favor contacte con el administrador");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        response.put("message", "El cliente " + newClient.getNombre() + " ha sido creado con éxito!");
        response.put("cliente", newClient);

        return new ResponseEntity<Map <String, Object> >(response, HttpStatus.CREATED);
    }

    @PutMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
       Cliente clienteActual = this.clienteServie.findById(id);
       Cliente updateClient = null;
       Map<String, Object> response = new HashMap<>();

       // Valida los campos
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();

            /*result.getFieldErrors().forEach(err -> {
                errors.add("campo: '" + err.getField() + "' " + err.getDefaultMessage());
            });*/

            for (FieldError err: result.getFieldErrors()) {
                errors.add("campo: '" + err.getField() + "' " + err.getDefaultMessage());
            }

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

       if (clienteActual == null) {
           response.put("message", "El usuario con ID: " + id + ", no esta registrado en la base de datos");
           return new ResponseEntity<Map <String, Object>>(response, HttpStatus.NOT_FOUND);
       }

       try {
           clienteActual.setNombre(cliente.getNombre());
           clienteActual.setApellido(cliente.getApellido());
           clienteActual.setEmail(cliente.getEmail());
           updateClient = this.clienteServie.save(clienteActual);
       } catch (DataAccessException e) {
           response.put("message", "Error al actualiza usuario");
           response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
           return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }

       response.put("message", "El cliente con ID: " + id + ", se actualizo con éxito!");
       response.put("cliente", updateClient);

       return new ResponseEntity<Map <String, Object>>(response, HttpStatus.OK);
    }

    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            this.clienteServie.delete(id);
        } catch (DataAccessException e) {
            response.put("message", "Error al eliminar usuario");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "usuario eliminado con éxito!");

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

    }

}
