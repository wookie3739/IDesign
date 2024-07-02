package com.my.interrior.client.shop.vignette.repository;

import com.my.interrior.client.shop.vignette.entity.VignetteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VignetteRepository extends JpaRepository<VignetteEntity, Long> {
    boolean existsByNo(long no);
}
