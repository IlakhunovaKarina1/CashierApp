package com.grappim.cashier.data.repository

import com.grappim.cashier.R
import com.grappim.cashier.core.domain.Acceptance
import com.grappim.cashier.core.domain.Cashier
import com.grappim.cashier.core.domain.Outlet
import com.grappim.cashier.core.extensions.bigDecimalZero
import com.grappim.cashier.core.extensions.getStringForDbQuery
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.core.storage.GeneralPrefsDataStore
import com.grappim.cashier.data.db.dao.BasketDao
import com.grappim.cashier.data.db.dao.CategoryDao
import com.grappim.cashier.data.db.dao.ProductsDao
import com.grappim.cashier.data.db.entity.BasketProduct
import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.ui.acceptance.vm.AcceptanceStatus
import com.grappim.cashier.ui.menu.MenuItem
import com.grappim.cashier.ui.menu.MenuItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

interface GeneralRepository {

    suspend fun getOutlets(): Either<Throwable, List<Outlet>>
    suspend fun getCashiers(): Either<Throwable, List<Cashier>>

    suspend fun getCategories(): Either<Throwable, List<Category>>
    suspend fun getProducts(): Either<Throwable, List<Product>>
    suspend fun getProductsByCategory(category: Category): List<Product>

    suspend fun clearBasket()
    suspend fun addBasketProduct(product: Product)
    suspend fun removeBasketProduct(product: Product)
    fun getAllBasketProducts(): Flow<List<BasketProduct>>

    fun getMenuItems(): Flow<List<MenuItem>>
    fun getAcceptanceList(): Flow<List<Acceptance>>

    suspend fun saveCashier(cashier: Cashier)
    suspend fun saveOutlet(outlet: Outlet)

    suspend fun searchProducts(query: String): List<Product>

    suspend fun prePopulateDb()

}

@Singleton
class GeneralRepositoryImpl @Inject constructor(
    private val basketDao: BasketDao,
    private val productsDao: ProductsDao,
    private val categoryDao: CategoryDao,
    private val generalPrefsDataStore: GeneralPrefsDataStore
) : GeneralRepository, BaseRepository() {

    override suspend fun getOutlets(): Either<Throwable, List<Outlet>> =
        Either.Right(getOutletList())

    override suspend fun getCashiers(): Either<Throwable, List<Cashier>> =
        Either.Right(getCashierList())

    override suspend fun getCategories(): Either<Throwable, List<Category>> =
        Either.Right(getCategoryList())

    override suspend fun getProducts(): Either<Throwable, List<Product>> =
        Either.Right(getProductList())

    override suspend fun getProductsByCategory(category: Category): List<Product> =
        withContext(Dispatchers.IO) {
            if (category.uid == "0") {
                productsDao.getAllProducts()
            } else {
                productsDao.searchProductsByCategoryId(category.uid)
            }
        }

    override suspend fun clearBasket() =
        basketDao.clearBasket()

    override suspend fun addBasketProduct(product: Product) {
        basketDao.insertOrUpdate(product.toBasketProduct())
    }

    override suspend fun removeBasketProduct(product: Product) {
        if (product.basketCount <= bigDecimalZero()) {
            basketDao.removeProductByUid(product.uid)
        } else {
            basketDao.updateBasketProduct(product.toBasketProduct())
        }
    }

    override fun getAllBasketProducts(): Flow<List<BasketProduct>> =
        basketDao.getAllBasketProducts()

    override fun getMenuItems(): Flow<List<MenuItem>> = flow {
        emit(
            listOf(
                MenuItem(
                    type = MenuItemType.SALES,
                    text = R.string.title_sales,
                    image = R.drawable.ic_cash_register
                ),
                MenuItem(
                    type = MenuItemType.PRODUCTS,
                    text = R.string.title_products,
                    image = R.drawable.ic_store
                ),
                MenuItem(
                    type = MenuItemType.ACCEPTANCE,
                    text = R.string.tittle_acceptance,
                    image = R.drawable.ic_store_acceptance
                )
            )
        )
    }

    override fun getAcceptanceList(): Flow<List<Acceptance>> = flow {
        val list = mutableListOf<Acceptance>()
        (0..20).forEach {
            list.add(
                Acceptance(
                    id = UUID.randomUUID().toString(),
                    vendorName = "Vendor Name $it",
                    date = "30.04.21 22:32",
                    status = getRandomAcceptanceStatus(),
                    toPay = BigDecimal("${Random.nextInt(50, 400)}"),
                    paidSum = BigDecimal("${Random.nextInt(50, 400)}")
                )
            )
        }
        emit(list)
    }.flowOn(Dispatchers.IO)

    private fun getRandomAcceptanceStatus(): AcceptanceStatus = listOf(
        AcceptanceStatus.PAID,
        AcceptanceStatus.STANDARD
    ).random()

    override suspend fun saveCashier(cashier: Cashier) = withContext(Dispatchers.IO) {
        generalPrefsDataStore.setCashierInfo(cashier)
        delay(50)
    }

    override suspend fun saveOutlet(outlet: Outlet) = withContext(Dispatchers.IO) {
        generalPrefsDataStore.setOutletInfo(outlet)
    }

    override suspend fun searchProducts(query: String): List<Product> =
        withContext(Dispatchers.IO) {
            val products = productsDao.searchProducts(query.getStringForDbQuery())

            val productsUids = products.map { it.uid }
            val storedBasketProducts = basketDao.getProductsByUids(productsUids)

            return@withContext if (storedBasketProducts.isEmpty()) {
                products
            } else {
                val resultList: List<Product> = products
                resultList.forEach { product ->
                    storedBasketProducts.forEach { storedProduct ->
                        if (storedProduct.uid == product.uid) {
                            product.basketCount = storedProduct.basketCount
                        }
                    }
                }
                resultList
            }
        }

    override suspend fun prePopulateDb() = withContext(Dispatchers.IO) {
        val products = mutableListOf<Product>()
        (0..20).forEach {
            products.add(
                Product(
                    uid = "$it",
                    name = "Product $it",
                    imageUrl = "",
                    price = BigDecimal("${Random.nextInt(50, 400)}"),
                    stockCount = BigDecimal("${Random.nextInt(0, 10)}"),
                    categoryId = Random.nextInt(2, 8).toString()
                )
            )
        }
        productsDao.insert(products)

        val categories = mutableListOf<Category>()
        categories.add(
            Category(
                uid = "0",
                name = "All"
            )
        )
        (1..11).forEach {
            categories.add(
                Category(
                    uid = "$it",
                    name = "Category $it"
                )
            )
        }
        categoryDao.insert(categories)
    }

    private fun getOutletList(): List<Outlet> {
        val outlets = mutableListOf<Outlet>()
        (0..10).forEach {
            outlets.add(Outlet("Outlet $it"))
        }
        return outlets.toList()
    }

    private fun getCashierList(): List<Cashier> {
        val cashiers = mutableListOf<Cashier>()
        (0..10).forEach {
            cashiers.add(Cashier(id = "$it", name = "Cashier $it"))
        }
        return cashiers.toList()
    }

    private suspend fun getCategoryList(): List<Category> = categoryDao.getAllCategories()

    private suspend fun getProductList(): List<Product> = withContext(Dispatchers.IO) {
        val products = productsDao.getAllProducts()

        val productsUids = products.map { it.uid }
        val storedBasketProducts = basketDao.getProductsByUids(productsUids)

        return@withContext if (storedBasketProducts.isEmpty()) {
            products
        } else {
            val resultList: List<Product> = products
            resultList.forEach { product ->
                storedBasketProducts.forEach { storedProduct ->
                    if (storedProduct.uid == product.uid) {
                        product.basketCount = storedProduct.basketCount
                    }
                }
            }
            resultList
        }
    }

    fun Product.toBasketProduct(): BasketProduct =
        BasketProduct(
            uid = this.uid,
            name = this.name,
            basketCount = this.basketCount,
            imageUrl = this.imageUrl,
            price = this.price,
            categoryId = this.categoryId,
            stockCount = this.stockCount
        )

    fun BasketProduct.toProduct(): Product =
        Product(
            uid = this.uid,
            name = this.name,
            basketCount = this.basketCount,
            imageUrl = this.imageUrl,
            price = this.price,
            categoryId = this.categoryId,
            stockCount = this.stockCount
        )

}