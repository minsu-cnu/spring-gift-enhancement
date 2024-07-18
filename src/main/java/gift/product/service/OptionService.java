package gift.product.service;

import gift.product.dto.OptionDto;
import gift.product.dto.OptionResponse;
import gift.product.model.Option;
import gift.product.model.Product;
import gift.product.repository.OptionRepository;
import gift.product.repository.ProductRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<Option> getOptionAll() {
        return optionRepository.findAll();
    }

    public List<OptionResponse> getOptionAllByProductId(Long productId) {
        return optionRepository.findAllByProductId(productId);
    }

    public Option getOption(Long id) {
        return getValidatedOption(id);
    }

    @Transactional
    public Option insertOption(OptionDto optionDto) {
        validateRedundancyOptionName(optionDto.name(), optionDto.productId());
        Product product = getValidatedProduct(optionDto.productId());

        return optionRepository.save(new Option(optionDto.name(), optionDto.quentity(), product));
    }

    @Transactional
    public Option updateOption(Long id, OptionDto optionDto) {
        Product product = getValidatedProduct(optionDto.productId());
        getValidatedOption(id);
        
        return optionRepository.save(new Option(id, optionDto.name(), optionDto.quentity(), product));
    }

    @Transactional
    public void deleteOption(Long id) {
        getValidatedOption(id);
        optionRepository.deleteById(id);
    }

    private Product getValidatedProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 상품이 존재하지 않습니다."));
    }

    private Option getValidatedOption(Long id) {
        return optionRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 옵션이 존재하지 않습니다."));
    }

    private void validateRedundancyOptionName(String optionName, Long productId) {
        if (optionRepository.existsByNameAndProductId(optionName, productId)) {
            throw new IllegalArgumentException("해당 상품에 이미 존재하는 옵션입니다.");
        }
    }
}