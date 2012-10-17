package com.wineshop.services;

import org.granite.messaging.service.annotations.RemoteDestination;
import org.granite.tide.spring.data.FilterableJpaRepository;

import com.wineshop.entities.Vineyard;


@RemoteDestination
public interface VineyardRepository extends FilterableJpaRepository<Vineyard, Long> {
}
