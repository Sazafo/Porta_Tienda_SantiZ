/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tienda.demo.service;
import Tienda.demo.domain.Categoria;
import Tienda.demo.repository.CategoriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//IMPORTACIONES NUEVAS:
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
/**
 *
 * @author sazaf
 */


@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Transactional(readOnly=true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
public Optional<Categoria> getCategoria(Integer idCategoria) {
    return categoriaRepository.findById(idCategoria);
}

@Autowired
private FirebaseStorageService firebaseStorageService;

@Transactional
public void save(Categoria categoria, MultipartFile imagenFile) {
    categoria = categoriaRepository.save(categoria);
    if (!imagenFile.isEmpty()) { //Si no está vacío... pasaron una imagen...
        try {
            String rutaImagen = firebaseStorageService.uploadImage(
                imagenFile, "categoria",
                categoria.getIdCategoria());
            categoria.setRutaImagen(rutaImagen);
            categoriaRepository.save(categoria);
        } catch (IOException e) {
        }
    }
}

@Transactional
public void delete(Integer idCategoria) {
    // Verifica si la categoria existe antes de intentar eliminarlo
    if (!categoriaRepository.existsById(idCategoria)) {
        // Lanza una excepción para indicar que el usuario no fue encontrado
        throw new IllegalArgumentException("La categoria con ID " + idCategoria + " no existe.");
    }
    try {
        categoriaRepository.deleteById(idCategoria);
    } catch (DataIntegrityViolationException e) {
        // Lanza una nueva excepción para encapsular el problema de integridad de datos
        throw new IllegalStateException("No se puede eliminar la categoria. Tiene datos asociados.", e);
    }
}
}

