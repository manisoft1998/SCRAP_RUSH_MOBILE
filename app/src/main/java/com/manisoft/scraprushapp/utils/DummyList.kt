package com.manisoft.scraprushapp.utils


object DummyList {
    fun getAddressTypes(): ArrayList<String> {
        val list: ArrayList<String> = arrayListOf()
        list.add("Home")
        list.add("Work")
        list.add("Other")
        return list
    }

    /*    fun getScrapRateList(): ArrayList<ScrapRate> {
            val list: ArrayList<ScrapRate> = arrayListOf()

            list.add(ScrapRate("Plastic", "1 kg", "₹25 onwards", R.drawable.plastic))
            list.add(ScrapRate("Paper", "1 kg", "₹15 onwards", R.drawable.paper))
            list.add(ScrapRate("E-Waste", "1 kg", "₹05 onwards", R.drawable.e_waste))

            list.add(ScrapRate("Plastic", "1 kg", "₹25 onwards", R.drawable.plastic))
            list.add(ScrapRate("Paper", "1 kg", "₹15 onwards", R.drawable.paper))
            list.add(ScrapRate("E-Waste", "1 kg", "₹05 onwards", R.drawable.e_waste))

            list.add(ScrapRate("Plastic", "1 kg", "₹25 onwards", R.drawable.plastic))
            list.add(ScrapRate("Paper", "1 kg", "₹15 onwards", R.drawable.paper))
            list.add(ScrapRate("E-Waste", "1 kg", "₹05 onwards", R.drawable.e_waste))

            return list
        }*/
}
