(ns my-math.images)

(def animals ["/images/background/2188233670_e4b3f052f0_k.jpg"
              "/images/background/2939051458_6e7774bc1f_h.jpg"
              "/images/background/3167480982_60752e1678_k.jpg"
              "/images/background/4874058038_106e2f2cbe_o.jpg"
              "/images/background/6445506933_668fe5b60b_o.jpg"
              "/images/background/6445507767_7f696aea4d_k.jpg"
              "/images/background/6445512445_449277f918_k.jpg"
              "/images/background/6445516721_0cb0aa89fa_k.jpg"
              "/images/background/6757523563_977592bbaf_h.jpg"
              "/images/background/8870463675_de013d4154_k.jpg"
              "/images/background/10672372336_70911963d9_k.jpg"
              "/images/background/12293406745_f42b806532_k.jpg"
              "/images/background/21723659353_988bc0203d_k.jpg"
              "/images/background/22906635290_2c0dfd4d72_k.jpg"
              "/images/background/23638111112_8d9645d5ed_k.jpg"
              "/images/background/24840887820_a5e5c11b07_k.jpg"
              "/images/background/26231747536_61851f5127_h.jpg"
              "/images/background/26786786413_f86e75c219_k.jpg"
              "/images/background/27295659492_7c0ff55aca_h.jpg"
              "/images/background/29013325977_d1e7c781c4_k.jpg"
              "/images/background/29227858315_a99170cdda_k.jpg"
              "/images/background/32564765748_7000c76bd1_k.jpg"
              "/images/background/33713580808_e04f558048_h.jpg"
              "/images/background/34020920692_adabc742ec_k.jpg"
              "/images/background/39829630032_fb06f72cc1_k.jpg"
              "/images/background/40271434945_3ce3bd3f3c_k.jpg"
              "/images/background/44028303820_3951b1b07b_k.jpg"
              "/images/background/45438289135_7a605a261b_k.jpg"])

(defn get-random-image []
  (let [count (count animals)
        rand-index (rand-int count)]
    (get animals rand-index)))
