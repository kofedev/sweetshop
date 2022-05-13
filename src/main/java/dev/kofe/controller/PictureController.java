package dev.kofe.controller;

/**
 *
 * PictureController
 *
 * GET: /getitemimg/{id}
 *
 */

import dev.kofe.model.Item;
import dev.kofe.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PictureController {

    @Autowired
    private ItemService itemService;

    @GetMapping(value = "/getitemimg/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImageWithMediaType(@PathVariable("id") long itemId) {
        Item item = itemService.getById(itemId);
        if (item.getPhoto() != null && item.getPhoto().length > 0) {
            return item.getPhoto();
        }

        return null;
    }
}
