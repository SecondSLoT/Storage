package com.secondslot.storage.domain.FakeDataForDb

import com.github.javafaker.Faker
import com.secondslot.storage.data.repository.model.Character

class FakeDataForDb {

    private val faker = Faker()

    fun generateFakeData(numberOfEntities: Int): List<Character> {
        val list = arrayListOf<Character>()
        repeat(numberOfEntities) {
            list.add(generateCharacter())
        }
        return list
    }

    private fun generateCharacter(): Character {
        return Character(
            name = faker.rickAndMorty().character(),
            location = faker.rickAndMorty().location(),
            quote = faker.rickAndMorty().quote()
        )
    }
}