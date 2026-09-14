<template>
  <div class="card">
    <h2>Adicionar Comentário</h2>
    <form @submit.prevent="enviar">
      <div class="form-group">
        <label>Aluno</label>
        <select v-model="alunoId" required>
          <option value="" disabled>Selecione um aluno</option>
          <option v-for="a in alunos" :key="a.id" :value="a.id">{{ a.nome }}</option>
        </select>
      </div>

      <div class="form-group">
        <label>Texto do comentário</label>
        <input v-model="texto" placeholder="Digite o comentário..." required />
      </div>

      <div class="form-group">
        <label>Tipo</label>
        <div class="tipo-buttons">
          <button
            type="button"
            :class="['btn', util ? 'btn-success' : 'btn-outline']"
            @click="util = true"
          >Útil</button>
          <button
            type="button"
            :class="['btn', !util ? 'btn-danger' : 'btn-outline']"
            @click="util = false"
          >Não Útil</button>
        </div>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="loading">
        {{ loading ? 'Enviando...' : 'Adicionar Comentário' }}
      </button>

      <div v-if="msg" :class="['alert', erro ? 'alert-error' : 'alert-success']">{{ msg }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ alunos: Array })
const emit = defineEmits(['comentario-adicionado'])

const alunoId = ref('')
const texto = ref('')
const util = ref(true)
const loading = ref(false)
const msg = ref('')
const erro = ref(false)

async function enviar() {
  loading.value = true
  msg.value = ''
  try {
    const res = await fetch(`/api/forum-rewards/alunos/${alunoId.value}/comentarios?util=${util.value}&texto=${encodeURIComponent(texto.value)}`, {
      method: 'POST'
    })
    if (!res.ok) throw new Error()
    texto.value = ''
    msg.value = `Comentário ${util.value ? 'útil' : 'não útil'} adicionado com sucesso!`
    erro.value = false
    emit('comentario-adicionado')
  } catch {
    msg.value = 'Erro ao adicionar comentário.'
    erro.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.tipo-buttons {
  display: flex;
  gap: 8px;
}

.btn-outline {
  background: white;
  color: #555;
  border: 1px solid #ccc;
}

.btn-danger {
  background: #e53935;
  color: white;
  border: none;
}
</style>
